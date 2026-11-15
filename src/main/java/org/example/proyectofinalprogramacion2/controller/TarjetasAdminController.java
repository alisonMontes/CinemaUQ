package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.service.AdministracionService;
import org.example.proyectofinalprogramacion2.service.TarjetaService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pestaña Tarjetas: recargar, activar o bloquear la tarjeta de un cliente y ver sus movimientos.
 */
public class TarjetasAdminController implements PestanaAdmin {

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colTarjeta;
    @FXML
    private TableColumn<Cliente, String> colSaldo;
    @FXML
    private TableColumn<Cliente, String> colEstado;
    @FXML
    private Label lblClienteElegido;
    @FXML
    private TextField txtMonto;
    @FXML
    private TableView<MovimientoTarjeta> tablaMovimientos;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colMovFecha;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colMovTipo;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colMovMonto;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colMovDescripcion;
    @FXML
    private Label lblMensaje;

    private final AdministracionService administracionService = new AdministracionService();
    private final TarjetaService tarjetaService = new TarjetaService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colTarjeta.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTarjeta().getNumero()));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty(
                Formato.dinero(tarjetaService.consultarSaldo(c.getValue()))));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTarjeta().getEstado().toString()));

        colMovFecha.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFecha())));
        colMovTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colMovMonto.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getMonto())));
        colMovDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));

        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, cliente) -> mostrarCliente(cliente));
    }

    @Override
    public void inicializar(AdminController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        try {
            Cliente seleccionado = tablaClientes.getSelectionModel().getSelectedItem();
            tablaClientes.setItems(FXCollections.observableArrayList(
                    administracionService.consultarClientes(principal.getAdministrador())));
            tablaClientes.refresh();
            if (seleccionado != null) {
                tablaClientes.getSelectionModel().select(seleccionado);
            }
            mostrarCliente(tablaClientes.getSelectionModel().getSelectedItem());
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    private void mostrarCliente(Cliente cliente) {
        if (cliente == null) {
            lblClienteElegido.setText("Selecciona un cliente");
            tablaMovimientos.getItems().clear();
            return;
        }
        lblClienteElegido.setText(cliente.getNombre() + " - tarjeta " + cliente.getTarjeta().getNumero() + " - "
                + Formato.dinero(tarjetaService.consultarSaldo(cliente)) + " (" + cliente.getTarjeta().getEstado() + ")");
        // Los más recientes primero.
        List<MovimientoTarjeta> movimientos = new ArrayList<>(tarjetaService.consultarMovimientos(cliente));
        Collections.reverse(movimientos);
        tablaMovimientos.setItems(FXCollections.observableArrayList(movimientos));
        tablaMovimientos.refresh();
    }

    private Cliente clienteElegido() {
        Cliente cliente = tablaClientes.getSelectionModel().getSelectedItem();
        if (cliente == null) {
            Dialogos.error("Selecciona en la tabla el cliente.");
        }
        return cliente;
    }

    @FXML
    private void recargar() {
        Cliente cliente = clienteElegido();
        if (cliente == null) {
            return;
        }
        try {
            double monto = Campos.dinero(txtMonto, "monto");
            tarjetaService.recargar(principal.getAdministrador(), cliente, monto, LocalDateTime.now());
            txtMonto.clear();
            refrescar();
            lblMensaje.setText("Recarga de " + Formato.dinero(monto) + " aplicada a " + cliente.getNombre());
        } catch (CinemaUQException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void activar() {
        Cliente cliente = clienteElegido();
        if (cliente == null) {
            return;
        }
        try {
            tarjetaService.activar(principal.getAdministrador(), cliente);
            refrescar();
            lblMensaje.setText("Tarjeta de " + cliente.getNombre() + " activada");
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    @FXML
    private void bloquear() {
        Cliente cliente = clienteElegido();
        if (cliente == null) {
            return;
        }
        if (!Dialogos.confirmar("Bloquear tarjeta", "¿Deseas bloquear la tarjeta de " + cliente.getNombre()
                + "? No podrá pagar compras hasta que se active de nuevo.")) {
            return;
        }
        try {
            tarjetaService.bloquear(principal.getAdministrador(), cliente);
            refrescar();
            lblMensaje.setText("Tarjeta de " + cliente.getNombre() + " bloqueada");
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
