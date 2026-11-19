package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.service.AdministracionService;

/**
 * Pestaña Películas: crear, editar y eliminar películas.
 */
public class PeliculasAdminController implements PestanaAdmin {

    @FXML
    private TableView<Pelicula> tablaPeliculas;
    @FXML
    private TableColumn<Pelicula, String> colCodigo;
    @FXML
    private TableColumn<Pelicula, String> colTitulo;
    @FXML
    private TableColumn<Pelicula, String> colGenero;
    @FXML
    private TableColumn<Pelicula, String> colDuracion;
    @FXML
    private TableColumn<Pelicula, String> colClasificacion;
    @FXML
    private TableColumn<Pelicula, String> colEnCartelera;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtGenero;
    @FXML
    private TextField txtDuracion;
    @FXML
    private TextField txtClasificacion;
    @FXML
    private TextArea txtSinopsis;
    @FXML
    private CheckBox chkEnCartelera;
    @FXML
    private Label lblMensaje;

    private final AdministracionService administracionService = new AdministracionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        colDuracion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDuracionMinutos() + " min"));
        colClasificacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasificacion()));
        colEnCartelera.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isEnCartelera() ? "Sí" : "No"));

        tablaPeliculas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, pelicula) -> mostrar(pelicula));
    }

    @Override
    public void inicializar(AdminController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        try {
            tablaPeliculas.setItems(FXCollections.observableArrayList(
                    administracionService.listarPeliculas(principal.getAdministrador())));
            tablaPeliculas.refresh();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    private void mostrar(Pelicula pelicula) {
        if (pelicula == null) {
            return;
        }
        txtCodigo.setText(pelicula.getCodigo());
        txtCodigo.setDisable(true);
        txtTitulo.setText(pelicula.getTitulo());
        txtGenero.setText(pelicula.getGenero());
        txtDuracion.setText(String.valueOf(pelicula.getDuracionMinutos()));
        txtClasificacion.setText(pelicula.getClasificacion());
        txtSinopsis.setText(pelicula.getSinopsis());
        chkEnCartelera.setSelected(pelicula.isEnCartelera());
        lblMensaje.setText("");
    }

    /** Botón para limpiar el formulario y escribir una película nueva. */
    @FXML
    private void nueva() {
        tablaPeliculas.getSelectionModel().clearSelection();
        txtCodigo.setDisable(false);
        for (TextField campo : new TextField[]{txtCodigo, txtTitulo, txtGenero, txtDuracion, txtClasificacion}) {
            campo.clear();
        }
        txtSinopsis.clear();
        chkEnCartelera.setSelected(true);
        lblMensaje.setText("");
    }

    /** Botón para crear la película con los datos del formulario. */
    @FXML
    private void crear() {
        try {
            Pelicula pelicula = new Pelicula(Campos.texto(txtCodigo), Campos.texto(txtTitulo),
                    Campos.texto(txtGenero), Campos.entero(txtDuracion, "duración"),
                    Campos.texto(txtClasificacion), Campos.texto(txtSinopsis));
            pelicula.setEnCartelera(chkEnCartelera.isSelected());
            administracionService.crearPelicula(principal.getAdministrador(), pelicula);
            refrescar();
            tablaPeliculas.getSelectionModel().select(pelicula);
            lblMensaje.setText("Película " + pelicula.getTitulo() + " creada");
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** Botón para guardar los cambios de la película seleccionada. */
    @FXML
    private void guardar() {
        Pelicula pelicula = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (pelicula == null) {
            Dialogos.error("Selecciona en la tabla la película que deseas editar.");
            return;
        }
        try {
            administracionService.actualizarPelicula(principal.getAdministrador(), pelicula.getCodigo(),
                    Campos.texto(txtTitulo), Campos.texto(txtGenero), Campos.entero(txtDuracion, "duración"),
                    Campos.texto(txtClasificacion), Campos.texto(txtSinopsis), chkEnCartelera.isSelected());
            tablaPeliculas.refresh();
            lblMensaje.setText("Cambios guardados");
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    /** Botón para eliminar la película seleccionada (pide confirmación). */
    @FXML
    private void eliminar() {
        Pelicula pelicula = tablaPeliculas.getSelectionModel().getSelectedItem();
        if (pelicula == null) {
            Dialogos.error("Selecciona en la tabla la película que deseas eliminar.");
            return;
        }
        if (!Dialogos.confirmar("Eliminar película", "¿Deseas eliminar " + pelicula.getTitulo() + "?")) {
            return;
        }
        try {
            administracionService.eliminarPelicula(principal.getAdministrador(), pelicula.getCodigo());
            nueva();
            refrescar();
            lblMensaje.setText("Película eliminada");
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
