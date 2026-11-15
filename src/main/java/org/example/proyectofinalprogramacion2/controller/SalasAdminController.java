package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.model.TipoAsiento;
import org.example.proyectofinalprogramacion2.service.AdministracionService;

/**
 * Pestaña Salas y asientos: crear salas y agregar o quitar asientos normales o accesibles.
 */
public class SalasAdminController implements PestanaAdmin {

    @FXML
    private TableView<Sala> tablaSalas;
    @FXML
    private TableColumn<Sala, String> colSalaCodigo;
    @FXML
    private TableColumn<Sala, String> colSalaNombre;
    @FXML
    private TableColumn<Sala, String> colSalaCapacidad;
    @FXML
    private TextField txtSalaCodigo;
    @FXML
    private TextField txtSalaNombre;
    @FXML
    private Label lblSalaElegida;
    @FXML
    private TableView<Asiento> tablaAsientos;
    @FXML
    private TableColumn<Asiento, String> colAsientoCodigo;
    @FXML
    private TableColumn<Asiento, String> colAsientoFila;
    @FXML
    private TableColumn<Asiento, String> colAsientoNumero;
    @FXML
    private TableColumn<Asiento, String> colAsientoTipo;
    @FXML
    private TextField txtFila;
    @FXML
    private TextField txtNumero;
    @FXML
    private ComboBox<TipoAsiento> cmbTipo;
    @FXML
    private Label lblMensaje;

    private final AdministracionService administracionService = new AdministracionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colSalaCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colSalaNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colSalaCapacidad.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCapacidad())));

        colAsientoCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colAsientoFila.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFila()));
        colAsientoNumero.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getNumero())));
        colAsientoTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));

        cmbTipo.setItems(FXCollections.observableArrayList(TipoAsiento.values()));
        cmbTipo.setValue(TipoAsiento.NORMAL);

        tablaSalas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, sala) -> mostrarAsientos(sala));
    }

    @Override
    public void inicializar(AdminController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        try {
            Sala seleccionada = tablaSalas.getSelectionModel().getSelectedItem();
            tablaSalas.setItems(FXCollections.observableArrayList(
                    administracionService.listarSalas(principal.getAdministrador())));
            tablaSalas.refresh();
            if (seleccionada != null) {
                tablaSalas.getSelectionModel().select(seleccionada);
            }
            mostrarAsientos(tablaSalas.getSelectionModel().getSelectedItem());
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    private void mostrarAsientos(Sala sala) {
        if (sala == null) {
            lblSalaElegida.setText("Selecciona una sala para ver y editar sus asientos");
            tablaAsientos.getItems().clear();
            return;
        }
        lblSalaElegida.setText("Asientos de " + sala.getNombre() + " (" + sala.getCapacidad() + ")");
        tablaAsientos.setItems(FXCollections.observableArrayList(sala.getAsientos()));
        tablaAsientos.refresh();
    }

    @FXML
    private void crearSala() {
        try {
            Sala sala = new Sala(Campos.texto(txtSalaCodigo), Campos.texto(txtSalaNombre));
            administracionService.crearSala(principal.getAdministrador(), sala);
            txtSalaCodigo.clear();
            txtSalaNombre.clear();
            refrescar();
            tablaSalas.getSelectionModel().select(sala);
            lblMensaje.setText("Sala " + sala.getNombre() + " creada; ahora agrégale asientos");
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void agregarAsiento() {
        Sala sala = tablaSalas.getSelectionModel().getSelectedItem();
        if (sala == null) {
            Dialogos.error("Selecciona la sala a la que deseas agregar el asiento.");
            return;
        }
        try {
            Asiento asiento = new Asiento(Campos.texto(txtFila), Campos.entero(txtNumero, "número"),
                    Campos.seleccion(cmbTipo, "el tipo de asiento"));
            administracionService.agregarAsiento(principal.getAdministrador(), sala.getCodigo(), asiento);
            txtNumero.clear();
            refrescar();
            lblMensaje.setText("Asiento " + asiento + " agregado a " + sala.getNombre());
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void eliminarAsiento() {
        Sala sala = tablaSalas.getSelectionModel().getSelectedItem();
        Asiento asiento = tablaAsientos.getSelectionModel().getSelectedItem();
        if (sala == null || asiento == null) {
            Dialogos.error("Selecciona la sala y el asiento que deseas eliminar.");
            return;
        }
        if (!Dialogos.confirmar("Eliminar asiento", "¿Deseas eliminar el asiento " + asiento.getCodigo()
                + " de " + sala.getNombre() + "?")) {
            return;
        }
        try {
            administracionService.eliminarAsiento(principal.getAdministrador(), sala.getCodigo(),
                    asiento.getFila(), asiento.getNumero());
            refrescar();
            lblMensaje.setText("Asiento " + asiento.getCodigo() + " eliminado");
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
