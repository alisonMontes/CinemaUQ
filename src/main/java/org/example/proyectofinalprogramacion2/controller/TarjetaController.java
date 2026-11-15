package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.TarjetaVirtual;
import org.example.proyectofinalprogramacion2.service.TarjetaService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pestaña Mi tarjeta: saldo, estado y movimientos de la tarjeta virtual.
 */
public class TarjetaController implements PestanaCliente {

    @FXML
    private Label lblNumero;
    @FXML
    private Label lblSaldo;
    @FXML
    private Label lblEstado;
    @FXML
    private TableView<MovimientoTarjeta> tablaMovimientos;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colFecha;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colTipo;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colMonto;
    @FXML
    private TableColumn<MovimientoTarjeta, String> colDescripcion;

    private final TarjetaService tarjetaService = new TarjetaService();
    private ClienteController principal;

    @FXML
    private void initialize() {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(Formato.fechaHora(c.getValue().getFecha())));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo().toString()));
        colMonto.setCellValueFactory(c -> new SimpleStringProperty(Formato.dinero(c.getValue().getMonto())));
        colDescripcion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescripcion()));
    }

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        Cliente cliente = principal.getCliente();
        TarjetaVirtual tarjeta = cliente.getTarjeta();
        lblNumero.setText(tarjeta.getNumero());
        lblSaldo.setText(Formato.dinero(tarjetaService.consultarSaldo(cliente)));
        lblEstado.setText(tarjeta.getEstado().toString());
        lblEstado.getStyleClass().removeAll("estado-activa", "estado-bloqueada");
        lblEstado.getStyleClass().add(tarjeta.estaActiva() ? "estado-activa" : "estado-bloqueada");

        // Los más recientes primero.
        List<MovimientoTarjeta> movimientos = new ArrayList<>(tarjetaService.consultarMovimientos(cliente));
        Collections.reverse(movimientos);
        tablaMovimientos.setItems(FXCollections.observableArrayList(movimientos));
        tablaMovimientos.refresh();
    }
}
