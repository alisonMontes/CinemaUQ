package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.service.AdministracionService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pestaña Funciones: crear, listar y eliminar funciones.
 */
public class FuncionesAdminController implements PestanaAdmin {

    @FXML
    private TableView<Funcion> tablaFunciones;
    @FXML
    private TableColumn<Funcion, String> colCodigo;
    @FXML
    private TableColumn<Funcion, String> colPelicula;
    @FXML
    private TableColumn<Funcion, String> colSala;
    @FXML
    private TableColumn<Funcion, String> colFechaHora;
    @FXML
    private TableColumn<Funcion, String> colPrecio;
    @FXML
    private TableColumn<Funcion, String> colOcupados;
    @FXML
    private TableColumn<Funcion, String> colEstado;
    @FXML
    private TextField txtCodigo;
    @FXML
    private ComboBox<Pelicula> cmbPelicula;
    @FXML
    private ComboBox<Sala> cmbSala;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TextField txtHora;
    @FXML
    private TextField txtPrecio;
    @FXML
    private Label lblMensaje;

    private final AdministracionService administracionService = new AdministracionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPelicula().getTitulo()));
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        colFechaHora.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFechaHora())));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecioBase())));
        colOcupados.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsientosOcupados().size()
                + " de " + c.getValue().getSala().getCapacidad()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));
    }

    @Override
    public void inicializar(AdminController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        try {
            List<Funcion> funciones = new ArrayList<>(administracionService.listarFunciones(principal.getAdministrador()));
            funciones.sort(Comparator.comparing(Funcion::getFechaHora));
            tablaFunciones.setItems(FXCollections.observableArrayList(funciones));
            tablaFunciones.refresh();

            Pelicula pelicula = cmbPelicula.getValue();
            Sala sala = cmbSala.getValue();
            cmbPelicula.setItems(FXCollections.observableArrayList(
                    administracionService.listarPeliculas(principal.getAdministrador())));
            cmbSala.setItems(FXCollections.observableArrayList(
                    administracionService.listarSalas(principal.getAdministrador())));
            cmbPelicula.setValue(pelicula);
            cmbSala.setValue(sala);
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void crear() {
        try {
            Funcion funcion = new Funcion(Campos.texto(txtCodigo), Campos.seleccion(cmbPelicula, "la película"),
                    Campos.seleccion(cmbSala, "la sala"), Campos.fecha(dpFecha, "fecha"), Campos.hora(txtHora),
                    Campos.dinero(txtPrecio, "precio base"));
            administracionService.crearFuncion(principal.getAdministrador(), funcion);
            txtCodigo.clear();
            refrescar();
            tablaFunciones.getSelectionModel().select(funcion);
            lblMensaje.setText("Función " + funcion.getCodigo() + " creada");
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.error("Selecciona en la tabla la función que deseas eliminar.");
            return;
        }
        if (!Dialogos.confirmar("Eliminar función", "¿Deseas eliminar la función " + funcion.getCodigo() + " ("
                + funcion.getPelicula().getTitulo() + ", " + Formato.fechaHora(funcion.getFechaHora()) + ")?")) {
            return;
        }
        try {
            administracionService.eliminarFuncion(principal.getAdministrador(), funcion.getCodigo());
            refrescar();
            lblMensaje.setText("Función " + funcion.getCodigo() + " eliminada");
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
