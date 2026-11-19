package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.service.CarteleraService;

import java.time.LocalDateTime;

/**
 * Pestaña Cartelera: películas en cartelera y sus funciones disponibles.
 */
public class CarteleraController implements PestanaCliente {

    @FXML
    private TableView<Pelicula> tablaPeliculas;
    @FXML
    private TableColumn<Pelicula, String> colTitulo;
    @FXML
    private TableColumn<Pelicula, String> colGenero;
    @FXML
    private TableColumn<Pelicula, String> colDuracion;
    @FXML
    private TableColumn<Pelicula, String> colClasificacion;
    @FXML
    private Label lblSinopsis;
    @FXML
    private TableView<Funcion> tablaFunciones;
    @FXML
    private TableColumn<Funcion, String> colFechaHora;
    @FXML
    private TableColumn<Funcion, String> colSala;
    @FXML
    private TableColumn<Funcion, String> colPrecio;
    @FXML
    private TableColumn<Funcion, String> colDisponibles;
    @FXML
    private Label lblMensaje;

    private final CarteleraService carteleraService = new CarteleraService();
    private ClienteController principal;

    @FXML
    private void initialize() {
        colTitulo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitulo()));
        colGenero.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenero()));
        colDuracion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDuracionMinutos() + " min"));
        colClasificacion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClasificacion()));

        colFechaHora.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFechaHora())));
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        colPrecio.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getPrecioBase())));
        colDisponibles.setCellValueFactory(c -> new SimpleStringProperty(
                carteleraService.getAsientosDisponibles(c.getValue()).size() + " de "
                        + c.getValue().getSala().getCapacidad()));

        tablaPeliculas.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, pelicula) -> mostrarFunciones(pelicula));
    }

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        Pelicula seleccionada = tablaPeliculas.getSelectionModel().getSelectedItem();
        tablaPeliculas.setItems(FXCollections.observableArrayList(carteleraService.getPeliculasEnCartelera()));
        tablaPeliculas.refresh();
        if (seleccionada != null && tablaPeliculas.getItems().contains(seleccionada)) {
            tablaPeliculas.getSelectionModel().select(seleccionada);
        } else {
            tablaPeliculas.getSelectionModel().selectFirst();
        }
        mostrarFunciones(tablaPeliculas.getSelectionModel().getSelectedItem());
        lblMensaje.setText("");
    }

    private void mostrarFunciones(Pelicula pelicula) {
        if (pelicula == null) {
            lblSinopsis.setText("");
            tablaFunciones.getItems().clear();
            return;
        }
        lblSinopsis.setText(pelicula.getTitulo() + ": " + pelicula.getSinopsis());
        tablaFunciones.setItems(FXCollections.observableArrayList(
                carteleraService.getFuncionesDisponibles(pelicula, LocalDateTime.now())));
        tablaFunciones.refresh();
    }

    /** Botón para guardar la función elegida en el carrito y pasar a los asientos. */
    @FXML
    private void elegirFuncion() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            lblMensaje.setText("Selecciona una función de la tabla");
            return;
        }
        principal.getCarrito().setFuncion(funcion);
        principal.irAAsientos();
    }
}
