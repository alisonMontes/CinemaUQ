package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.Chart;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.report.GraficoEntradasPorPelicula;
import org.example.proyectofinalprogramacion2.report.GraficoOcupacionSalas;
import org.example.proyectofinalprogramacion2.report.GraficoVentasPorDia;
import org.example.proyectofinalprogramacion2.report.ReporteVisual;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pestaña Gráficos: entradas por película (barras), ocupación por sala (torta) y ventas por día (líneas).
 */
public class GraficosAdminController implements PestanaAdmin {

    /** Por defecto: el último mes de ventas y las funciones del mes siguiente. */
    private static final int DIAS_PERIODO_INICIAL = 30;

    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    @FXML
    private GridPane gridGraficos;

    private final List<ReporteVisual> graficos;

    public GraficosAdminController() {
        EstadisticaService estadisticas = new EstadisticaService();
        graficos = List.of(new GraficoEntradasPorPelicula(estadisticas), new GraficoOcupacionSalas(estadisticas),
                new GraficoVentasPorDia(estadisticas));
    }

    @FXML
    private void initialize() {
        LocalDate hoy = LocalDateTime.now().toLocalDate();
        dpDesde.setValue(hoy.minusDays(DIAS_PERIODO_INICIAL));
        dpHasta.setValue(hoy.plusDays(DIAS_PERIODO_INICIAL));
    }

    @Override
    public void inicializar(AdminController principal) {
        // Los gráficos no necesitan datos de la ventana principal.
    }

    @Override
    public void refrescar() {
        actualizar();
    }

    @FXML
    private void actualizar() {
        try {
            LocalDate desde = Campos.fecha(dpDesde, "fecha inicial");
            LocalDate hasta = Campos.fecha(dpHasta, "fecha final");
            Chart[] nuevos = new Chart[graficos.size()];
            for (int i = 0; i < graficos.size(); i++) {
                nuevos[i] = graficos.get(i).crearGrafico(desde, hasta);
                nuevos[i].getStyleClass().add("grafico");
            }
            gridGraficos.getChildren().clear();
            gridGraficos.add(nuevos[0], 0, 0);           // barras
            gridGraficos.add(nuevos[1], 1, 0);           // torta
            gridGraficos.add(nuevos[2], 0, 1, 2, 1);     // líneas, a lo ancho
        } catch (ReporteException | IllegalArgumentException e) {
            Dialogos.error(e.getMessage());
        }
    }
}
