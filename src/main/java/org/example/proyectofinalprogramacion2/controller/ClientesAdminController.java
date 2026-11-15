package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.service.AdministracionService;

import java.util.StringJoiner;

/**
 * Pestaña Clientes y compras: clientes registrados y las compras del cliente seleccionado.
 */
public class ClientesAdminController implements PestanaAdmin {

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, String> colTarjeta;
    @FXML
    private TableColumn<Cliente, String> colSaldo;
    @FXML
    private TableColumn<Cliente, String> colEstadoTarjeta;
    @FXML
    private TableColumn<Cliente, String> colCompras;
    @FXML
    private Label lblComprasDe;
    @FXML
    private TableView<Compra> tablaCompras;
    @FXML
    private TableColumn<Compra, String> colCompraCodigo;
    @FXML
    private TableColumn<Compra, String> colCompraFecha;
    @FXML
    private TableColumn<Compra, String> colCompraFuncion;
    @FXML
    private TableColumn<Compra, String> colCompraDetalle;
    @FXML
    private TableColumn<Compra, String> colCompraTotal;
    @FXML
    private TableColumn<Compra, String> colCompraPago;
    @FXML
    private TableColumn<Compra, String> colCompraEstado;

    private final AdministracionService administracionService = new AdministracionService();
    private AdminController principal;

    @FXML
    private void initialize() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colTarjeta.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTarjeta().getNumero()));
        colSaldo.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getTarjeta().getSaldo())));
        colEstadoTarjeta.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getTarjeta().getEstado().toString()));
        colCompras.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getCompras().size())));

        colCompraCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colCompraFecha.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFecha())));
        colCompraFuncion.setCellValueFactory(c -> new SimpleStringProperty(descripcionFuncion(c.getValue())));
        colCompraDetalle.setCellValueFactory(c -> new SimpleStringProperty(detalle(c.getValue())));
        colCompraTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isPagadaConPuntos()
                ? "-" : Formato.dinero(c.getValue().getTotal())));
        colCompraPago.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isPagadaConPuntos()
                ? "Puntos (" + c.getValue().getPuntosUsados() + ")" : "Tarjeta"));
        colCompraEstado.setCellValueFactory(c -> new SimpleStringProperty(estado(c.getValue())));

        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, cliente) -> mostrarCompras(cliente));
    }

    private String descripcionFuncion(Compra compra) {
        Funcion funcion = compra.getFuncion();
        if (funcion == null) {
            return "Canje (sin función)";
        }
        return funcion.getPelicula().getTitulo() + " - " + Formato.fechaHora(funcion.getFechaHora());
    }

    private String detalle(Compra compra) {
        StringJoiner items = new StringJoiner(", ");
        for (ItemVendible item : compra.getItems()) {
            items.add(item.getNombre());
        }
        return items.toString();
    }

    private String estado(Compra compra) {
        if (compra.getEstado() == EstadoCompra.REEMBOLSADA) {
            return "REEMBOLSADA (" + Formato.dinero(compra.getMontoReembolsado()) + ")";
        }
        return compra.getEstado().toString();
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
            mostrarCompras(tablaClientes.getSelectionModel().getSelectedItem());
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }

    private void mostrarCompras(Cliente cliente) {
        if (cliente == null) {
            lblComprasDe.setText("Selecciona un cliente para ver sus compras");
            tablaCompras.getItems().clear();
            return;
        }
        try {
            lblComprasDe.setText("Compras de " + cliente.getNombre());
            tablaCompras.setItems(FXCollections.observableArrayList(
                    administracionService.consultarComprasDeCliente(principal.getAdministrador(), cliente)));
            tablaCompras.refresh();
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
