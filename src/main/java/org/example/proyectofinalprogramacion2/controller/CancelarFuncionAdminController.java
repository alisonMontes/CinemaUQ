package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.service.AdministracionService;
import org.example.proyectofinalprogramacion2.service.CancelacionService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Pestaña Cancelar función: el cine cancela una función y se muestran los reembolsos realizados.
 */
public class CancelarFuncionAdminController implements PestanaAdmin {

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
    private TableColumn<Funcion, String> colVendidos;
    @FXML
    private TableColumn<Funcion, String> colEstado;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblResultado;
    @FXML
    private TableView<Compra> tablaReembolsos;
    @FXML
    private TableColumn<Compra, String> colCompra;
    @FXML
    private TableColumn<Compra, String> colCliente;
    @FXML
    private TableColumn<Compra, String> colPagado;
    @FXML
    private TableColumn<Compra, String> colReembolsado;

    private final AdministracionService administracionService = new AdministracionService();
    private final CancelacionService cancelacionService = new CancelacionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colPelicula.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPelicula().getTitulo()));
        colSala.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSala().getNombre()));
        colFechaHora.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFechaHora())));
        colVendidos.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getAsientosOcupados().size())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().toString()));

        colCompra.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre()));
        colPagado.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getTotal())));
        colReembolsado.setCellValueFactory(c -> new SimpleStringProperty(
                Formato.dinero(c.getValue().getMontoReembolsado())));

        tablaFunciones.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, funcion) -> actualizarBoton(funcion));
    }

    /**
     * Solo se ofrece cancelar las funciones que no están canceladas.
     */
    private void actualizarBoton(Funcion funcion) {
        btnCancelar.setDisable(funcion == null || funcion.getEstado() == EstadoFuncion.CANCELADA);
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
            actualizarBoton(tablaFunciones.getSelectionModel().getSelectedItem());
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void cancelarFuncion() {
        Funcion funcion = tablaFunciones.getSelectionModel().getSelectedItem();
        if (funcion == null) {
            Dialogos.error("Selecciona en la tabla la función que deseas cancelar.");
            return;
        }
        if (!Dialogos.confirmar("Cancelar función " + funcion.getCodigo(), "¿Deseas cancelar la función de "
                + funcion.getPelicula().getTitulo() + " del " + Formato.fechaHora(funcion.getFechaHora())
                + " en " + funcion.getSala().getNombre() + "?\n\nSe reembolsará el 100 % de las compras pagadas"
                + " con tarjeta y se notificará a los clientes. Esta acción no se puede deshacer.")) {
            return;
        }
        try {
            List<Compra> reembolsadas = cancelacionService.cancelarFuncion(principal.getAdministrador(), funcion,
                    LocalDateTime.now());
            double total = 0;
            for (Compra compra : reembolsadas) {
                total += compra.getMontoReembolsado();
            }
            lblResultado.setText("Función " + funcion.getCodigo() + " cancelada. Compras reembolsadas: "
                    + reembolsadas.size() + " por un total de " + Formato.dinero(total) + ".");
            tablaReembolsos.setItems(FXCollections.observableArrayList(reembolsadas));
            tablaReembolsos.refresh();
            refrescar();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
