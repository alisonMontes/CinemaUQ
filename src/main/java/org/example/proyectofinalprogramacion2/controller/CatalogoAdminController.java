package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.service.AdministracionService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Pestaña Productos y combos: crear productos y armar combos con sus componentes y descuento.
 */
public class CatalogoAdminController implements PestanaAdmin {

    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, String> colProductoCodigo;
    @FXML
    private TableColumn<Producto, String> colProductoNombre;
    @FXML
    private TableColumn<Producto, String> colProductoPrecio;
    @FXML
    private TableColumn<Producto, String> colProductoDescripcion;
    @FXML
    private TextField txtProductoCodigo;
    @FXML
    private TextField txtProductoNombre;
    @FXML
    private TextField txtProductoPrecio;
    @FXML
    private TextField txtProductoDescripcion;
    @FXML
    private TableView<Combo> tablaCombos;
    @FXML
    private TableColumn<Combo, String> colComboCodigo;
    @FXML
    private TableColumn<Combo, String> colComboNombre;
    @FXML
    private TableColumn<Combo, String> colComboContenido;
    @FXML
    private TableColumn<Combo, String> colComboDescuento;
    @FXML
    private TableColumn<Combo, String> colComboPrecio;
    @FXML
    private TextField txtComboCodigo;
    @FXML
    private TextField txtComboNombre;
    @FXML
    private TextField txtComboDescuento;
    @FXML
    private ComboBox<ItemVendible> cmbComponente;
    @FXML
    private ListView<ItemVendible> listaComponentes;
    @FXML
    private Label lblMensaje;

    private final AdministracionService administracionService = new AdministracionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colProductoCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colProductoNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colProductoPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecio())));
        colProductoDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        colComboCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colComboNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colComboContenido.setCellValueFactory(c -> new SimpleStringProperty(contenido(c.getValue())));
        colComboDescuento.setCellValueFactory(c -> new SimpleStringProperty(
                Formato.porcentaje(c.getValue().getPorcentajeDescuento())));
        colComboPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecio())));

        cmbComponente.setConverter(new StringConverter<>() {
            @Override
            public String toString(ItemVendible item) {
                return item == null ? "" : descripcion(item);
            }

            @Override
            public ItemVendible fromString(String texto) {
                return null;
            }
        });
        listaComponentes.setCellFactory(lista -> new ListCell<>() {
            @Override
            protected void updateItem(ItemVendible item, boolean vacio) {
                super.updateItem(item, vacio);
                setText(vacio || item == null ? null : descripcion(item));
            }
        });
    }

    private String descripcion(ItemVendible item) {
        String tipo = item instanceof Combo ? "Combo: " : "";
        return tipo + item.getNombre() + " (" + Formato.dinero(item.getPrecio()) + ")";
    }

    private String contenido(Combo combo) {
        StringJoiner nombres = new StringJoiner(", ");
        for (ItemVendible componente : combo.getComponentes()) {
            nombres.add(componente.getNombre());
        }
        return nombres.toString();
    }

    @Override
    public void inicializar(AdminController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        try {
            List<Producto> productos = administracionService.listarProductos(principal.getAdministrador());
            List<Combo> combos = administracionService.listarCombos(principal.getAdministrador());
            tablaProductos.setItems(FXCollections.observableArrayList(productos));
            tablaProductos.refresh();
            tablaCombos.setItems(FXCollections.observableArrayList(combos));
            tablaCombos.refresh();

            List<ItemVendible> componentesPosibles = new ArrayList<>(productos);
            componentesPosibles.addAll(combos);
            cmbComponente.setItems(FXCollections.observableArrayList(componentesPosibles));
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** Botón para crear el producto con los datos del formulario. */
    @FXML
    private void crearProducto() {
        try {
            Producto producto = new Producto(Campos.texto(txtProductoCodigo), Campos.texto(txtProductoNombre),
                    Campos.dinero(txtProductoPrecio, "precio"), Campos.texto(txtProductoDescripcion));
            administracionService.crearProducto(principal.getAdministrador(), producto);
            for (TextField campo : new TextField[]{txtProductoCodigo, txtProductoNombre, txtProductoPrecio,
                    txtProductoDescripcion}) {
                campo.clear();
            }
            refrescar();
            lblMensaje.setText("Producto creado: " + producto.getNombre());
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** Botón para agregar el producto o combo elegido a los componentes del combo nuevo. */
    @FXML
    private void agregarComponente() {
        ItemVendible componente = cmbComponente.getValue();
        if (componente == null) {
            Dialogos.error("Elige el producto o combo que deseas agregar como componente.");
            return;
        }
        listaComponentes.getItems().add(componente);
    }

    /** Botón para quitar el componente seleccionado del combo nuevo. */
    @FXML
    private void quitarComponente() {
        int indice = listaComponentes.getSelectionModel().getSelectedIndex();
        if (indice < 0) {
            Dialogos.error("Selecciona en la lista el componente que deseas quitar.");
            return;
        }
        listaComponentes.getItems().remove(indice);
    }

    /** Botón para crear el combo con sus componentes y su descuento. */
    @FXML
    private void crearCombo() {
        try {
            Combo combo = new Combo(Campos.texto(txtComboCodigo), Campos.texto(txtComboNombre),
                    Campos.decimal(txtComboDescuento, "porcentaje de descuento"));
            for (ItemVendible componente : listaComponentes.getItems()) {
                combo.agregarComponente(componente);
            }
            administracionService.crearCombo(principal.getAdministrador(), combo);
            txtComboCodigo.clear();
            txtComboNombre.clear();
            txtComboDescuento.clear();
            listaComponentes.getItems().clear();
            refrescar();
            lblMensaje.setText("Combo creado: " + combo.getNombre() + " a " + Formato.dinero(combo.getPrecio()));
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
