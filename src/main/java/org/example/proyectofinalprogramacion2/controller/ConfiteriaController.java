package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.service.CarteleraService;

import java.util.StringJoiner;

/**
 * Pestaña Confitería y combos: agrega productos y combos al carrito.
 */
public class ConfiteriaController implements PestanaCliente {

    @FXML
    private TableView<Producto> tablaProductos;
    @FXML
    private TableColumn<Producto, String> colProductoNombre;
    @FXML
    private TableColumn<Producto, String> colProductoDescripcion;
    @FXML
    private TableColumn<Producto, String> colProductoPrecio;
    @FXML
    private TableView<Combo> tablaCombos;
    @FXML
    private TableColumn<Combo, String> colComboNombre;
    @FXML
    private TableColumn<Combo, String> colComboContenido;
    @FXML
    private TableColumn<Combo, String> colComboPrecio;
    @FXML
    private TableColumn<Combo, String> colComboAhorro;
    @FXML
    private TableView<ItemVendible> tablaCarrito;
    @FXML
    private TableColumn<ItemVendible, String> colItemNombre;
    @FXML
    private TableColumn<ItemVendible, String> colItemPrecio;
    @FXML
    private Label lblMensaje;

    private final CarteleraService carteleraService = new CarteleraService();
    private ClienteController principal;

    @FXML
    private void initialize() {
        colProductoNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colProductoDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
        colProductoPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecio())));

        colComboNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colComboContenido.setCellValueFactory(c -> new SimpleStringProperty(contenido(c.getValue())));
        colComboPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecio())));
        colComboAhorro.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getAhorro())));

        colItemNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colItemPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecio())));
    }

    private String contenido(Combo combo) {
        StringJoiner nombres = new StringJoiner(", ");
        for (ItemVendible componente : combo.getComponentes()) {
            nombres.add(componente.getNombre());
        }
        return nombres.toString();
    }

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        tablaProductos.setItems(FXCollections.observableArrayList(carteleraService.getProductos()));
        tablaProductos.refresh();
        tablaCombos.setItems(FXCollections.observableArrayList(carteleraService.getCombos()));
        tablaCombos.refresh();
        mostrarCarrito();
        lblMensaje.setText("");
    }

    private void mostrarCarrito() {
        tablaCarrito.setItems(FXCollections.observableArrayList(principal.getCarrito().getItemsConfiteria()));
        tablaCarrito.refresh();
    }

    @FXML
    private void agregarProducto() {
        Producto producto = tablaProductos.getSelectionModel().getSelectedItem();
        if (producto == null) {
            lblMensaje.setText("Selecciona un producto");
            return;
        }
        principal.getCarrito().agregarProducto(producto);
        lblMensaje.setText("");
        mostrarCarrito();
    }

    @FXML
    private void agregarCombo() {
        Combo combo = tablaCombos.getSelectionModel().getSelectedItem();
        if (combo == null) {
            lblMensaje.setText("Selecciona un combo");
            return;
        }
        principal.getCarrito().agregarCombo(combo);
        lblMensaje.setText("");
        mostrarCarrito();
    }

    @FXML
    private void quitarItem() {
        ItemVendible item = tablaCarrito.getSelectionModel().getSelectedItem();
        if (item == null) {
            lblMensaje.setText("Selecciona un ítem del carrito");
            return;
        }
        principal.getCarrito().quitarItem(item);
        lblMensaje.setText("");
        mostrarCarrito();
    }

    @FXML
    private void irAResumen() {
        principal.irAResumen();
    }
}
