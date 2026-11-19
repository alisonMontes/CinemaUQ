package org.example.proyectofinalprogramacion2.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;

/**
 * Pestaña Estadísticas: ventas e ingresos del periodo, ocupación, más vendidos y reembolsos,
 * en etiquetas y tablas.
 */
public class EstadisticasAdminController implements PestanaAdmin {

    private static final int DIAS_PERIODO_INICIAL = 30;

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private Label lblCompras;
    @FXML
    private Label lblIngresos;
    @FXML
    private Label lblReembolsado;
    @FXML
    private Label lblPuntos;

    @FXML
    private TableView<Map.Entry<LocalDate, Double>> tablaVentasDia;
    @FXML
    private TableColumn<Map.Entry<LocalDate, Double>, String> colDia;
    @FXML
    private TableColumn<Map.Entry<LocalDate, Double>, String> colVentasDia;

    @FXML
    private TableView<Map.Entry<Sala, Double>> tablaOcupacionSala;
    @FXML
    private TableColumn<Map.Entry<Sala, Double>, String> colSala;
    @FXML
    private TableColumn<Map.Entry<Sala, Double>, String> colOcupacionSala;

    @FXML
    private TableView<Map.Entry<Funcion, Double>> tablaOcupacionFuncion;
    @FXML
    private TableColumn<Map.Entry<Funcion, Double>, String> colFuncion;
    @FXML
    private TableColumn<Map.Entry<Funcion, Double>, String> colOcupacionFuncion;

    @FXML
    private TableView<Map.Entry<Pelicula, Integer>> tablaPeliculas;
    @FXML
    private TableColumn<Map.Entry<Pelicula, Integer>, String> colPelicula;
    @FXML
    private TableColumn<Map.Entry<Pelicula, Integer>, String> colEntradas;

    @FXML
    private TableView<Map.Entry<Producto, Integer>> tablaProductos;
    @FXML
    private TableColumn<Map.Entry<Producto, Integer>, String> colProducto;
    @FXML
    private TableColumn<Map.Entry<Producto, Integer>, String> colUnidadesProducto;

    @FXML
    private TableView<Map.Entry<Combo, Integer>> tablaCombos;
    @FXML
    private TableColumn<Map.Entry<Combo, Integer>, String> colCombo;
    @FXML
    private TableColumn<Map.Entry<Combo, Integer>, String> colUnidadesCombo;

    @FXML
    private TableView<Compra> tablaReembolsos;
    @FXML
    private TableColumn<Compra, String> colReembolsoCompra;
    @FXML
    private TableColumn<Compra, String> colReembolsoCliente;
    @FXML
    private TableColumn<Compra, String> colReembolsoMonto;

    private final EstadisticaService estadisticaService = new EstadisticaService();

    @FXML
    private void initialize() {
        columnas(colDia, Formato::fecha, colVentasDia, Formato::dinero);
        columnas(colSala, Sala::getNombre, colOcupacionSala, Formato::porcentaje);
        columnas(colFuncion, f -> f.getCodigo() + " - " + f.getPelicula().getTitulo() + " - "
                + Formato.fechaHora(f.getFechaHora()), colOcupacionFuncion, Formato::porcentaje);
        columnas(colPelicula, Pelicula::getTitulo, colEntradas, String::valueOf);
        columnas(colProducto, Producto::getNombre, colUnidadesProducto, String::valueOf);
        columnas(colCombo, Combo::getNombre, colUnidadesCombo, String::valueOf);

        colReembolsoCompra.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCodigo()));
        colReembolsoCliente.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCliente().getNombre()));
        colReembolsoMonto.setCellValueFactory(c -> new SimpleStringProperty(
                Formato.dinero(c.getValue().getMontoReembolsado())));

        LocalDate hoy = LocalDateTime.now().toLocalDate();
        dpDesde.setValue(hoy.minusDays(DIAS_PERIODO_INICIAL));
        dpHasta.setValue(hoy);
    }

    private <K, V> void columnas(TableColumn<Map.Entry<K, V>, String> colClave, Function<K, String> clave,
                                 TableColumn<Map.Entry<K, V>, String> colValor, Function<V, String> valor) {
        colClave.setCellValueFactory(c -> new SimpleStringProperty(clave.apply(c.getValue().getKey())));
        colValor.setCellValueFactory(c -> new SimpleStringProperty(valor.apply(c.getValue().getValue())));
    }

    private <K, V> void llenar(TableView<Map.Entry<K, V>> tabla, Map<K, V> datos) {
        tabla.setItems(FXCollections.observableArrayList(new ArrayList<>(datos.entrySet())));
        tabla.refresh();
    }

    @Override
    public void inicializar(AdminController principal) {
        // Las estadísticas no necesitan datos de la ventana principal.
    }

    @Override
    public void refrescar() {
        calcular();
    }

    /** Botón para calcular las estadísticas del periodo elegido. */
    @FXML
    private void calcular() {
        LocalDate desde;
        LocalDate hasta;
        try {
            desde = Campos.fecha(dpDesde, "fecha inicial");
            hasta = Campos.fecha(dpHasta, "fecha final");
        } catch (IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
            return;
        }
        if (desde.isAfter(hasta)) {
            Dialogos.error("La fecha inicial no puede ser posterior a la fecha final.");
            return;
        }
        lblCompras.setText(String.valueOf(estadisticaService.getComprasEnPeriodo(desde, hasta).size()));
        lblIngresos.setText(Formato.dinero(estadisticaService.getIngresosNetos(desde, hasta)));
        lblReembolsado.setText(Formato.dinero(estadisticaService.getTotalReembolsado(desde, hasta)));
        lblPuntos.setText(String.valueOf(estadisticaService.getPuntosRedimidos(desde, hasta)));

        llenar(tablaVentasDia, estadisticaService.getVentasPorDia(desde, hasta));
        llenar(tablaOcupacionSala, estadisticaService.getOcupacionPorSala());
        llenar(tablaOcupacionFuncion, estadisticaService.getOcupacionPorFuncion());
        llenar(tablaPeliculas, estadisticaService.getEntradasPorPelicula());
        llenar(tablaProductos, estadisticaService.getProductosMasVendidos());
        llenar(tablaCombos, estadisticaService.getCombosMasVendidos());

        tablaReembolsos.setItems(FXCollections.observableArrayList(
                estadisticaService.getComprasReembolsadas(desde, hasta)));
        tablaReembolsos.refresh();
    }
}
