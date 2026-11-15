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
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.service.CancelacionService;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * Pestaña Mis compras: historial del cliente y cancelación de compras.
 */
public class ComprasController implements PestanaCliente {

    @FXML
    private TableView<Compra> tablaCompras;
    @FXML
    private TableColumn<Compra, String> colCodigo;
    @FXML
    private TableColumn<Compra, String> colFecha;
    @FXML
    private TableColumn<Compra, String> colFuncion;
    @FXML
    private TableColumn<Compra, String> colDetalle;
    @FXML
    private TableColumn<Compra, String> colTotal;
    @FXML
    private TableColumn<Compra, String> colPago;
    @FXML
    private TableColumn<Compra, String> colEstado;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblMensaje;

    private final CancelacionService cancelacionService = new CancelacionService();
    private ClienteController principal;

    @FXML
    private void initialize() {
        colCodigo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFecha())));
        colFuncion.setCellValueFactory(c -> new SimpleStringProperty(descripcionFuncion(c.getValue())));
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(detalle(c.getValue())));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isPagadaConPuntos()
                ? "-" : Formato.dinero(c.getValue().getTotal())));
        colPago.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isPagadaConPuntos()
                ? "Puntos (" + c.getValue().getPuntosUsados() + ")" : "Tarjeta"));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(estado(c.getValue())));

        tablaCompras.getSelectionModel().selectedItemProperty()
                .addListener((obs, anterior, compra) -> actualizarBoton(compra));
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
        return compra.isPagadaConPuntos() ? compra.getEstado() + " - canje" : compra.getEstado().toString();
    }

    /**
     * Solo se ofrece cancelar las compras pagadas con tarjeta que siguen PAGADAS.
     */
    private void actualizarBoton(Compra compra) {
        btnCancelar.setDisable(compra == null || compra.isPagadaConPuntos()
                || compra.getEstado() != EstadoCompra.PAGADA);
    }

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        // Las más recientes primero.
        List<Compra> compras = new ArrayList<>(principal.getCliente().getCompras());
        Collections.reverse(compras);
        tablaCompras.setItems(FXCollections.observableArrayList(compras));
        tablaCompras.refresh();
        actualizarBoton(null);
        lblMensaje.setText("");
    }

    @FXML
    private void cancelarCompra() {
        Compra compra = tablaCompras.getSelectionModel().getSelectedItem();
        if (compra == null) {
            lblMensaje.setText("Selecciona una compra");
            return;
        }
        LocalDateTime ahora = LocalDateTime.now();
        ReembolsoStrategy politica = cancelacionService.consultarPolitica(compra, ahora);
        double aDevolver;
        try {
            aDevolver = politica.calcularReembolso(compra.getTotal());
        } catch (CinemaUQException e) {
            Dialogos.error(politica.getDescripcion() + ". " + e.getMessage());
            return;
        }
        boolean confirmado = Dialogos.confirmar("Cancelar compra " + compra.getCodigo(),
                "Política aplicable: " + politica.getDescripcion() + ".\n"
                        + "Se devolverán " + Formato.dinero(aDevolver) + " de " + Formato.dinero(compra.getTotal())
                        + " a tu tarjeta.\n\n¿Deseas cancelar la compra?");
        if (!confirmado) {
            return;
        }
        try {
            double devuelto = cancelacionService.cancelarCompra(principal.getCliente(), compra, LocalDateTime.now());
            Dialogos.info("Compra cancelada", "Se reembolsaron " + Formato.dinero(devuelto) + " a tu tarjeta.");
        } catch (CinemaUQException e) {
            Dialogos.error(e.getMessage());
        }
        refrescar();
    }
}
