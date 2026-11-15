package org.example.proyectofinalprogramacion2.report;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Gráfico de líneas: ingresos netos de cada día del periodo que tuvo ventas.
 */
// SRP: solo dibuja las ventas por día que calcula EstadisticaService.
public class GraficoVentasPorDia implements ReporteVisual {

    private final EstadisticaService estadisticas;

    public GraficoVentasPorDia(EstadisticaService estadisticas) {
        this.estadisticas = Objects.requireNonNull(estadisticas, "El servicio de estadísticas es obligatorio");
    }

    @Override
    public String getTitulo() {
        return "Ventas por día (ingresos netos)";
    }

    @Override
    public Chart crearGrafico(LocalDate desde, LocalDate hasta) throws ReporteException {
        ReporteVisual.validarPeriodo(desde, hasta);
        Map<LocalDate, Double> ventas = estadisticas.getVentasPorDia(desde, hasta);

        CategoryAxis ejeDias = new CategoryAxis();
        ejeDias.setLabel("Día");
        NumberAxis ejeIngresos = new NumberAxis();
        ejeIngresos.setLabel("Ingresos netos");
        ejeIngresos.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number valor) {
                return FormatoReporte.dinero(valor.doubleValue());
            }

            @Override
            public Number fromString(String texto) {
                return null;
            }
        });

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Ingresos netos");
        for (Map.Entry<LocalDate, Double> dia : ventas.entrySet()) {
            serie.getData().add(new XYChart.Data<>(FormatoReporte.fecha(dia.getKey()), dia.getValue()));
        }

        LineChart<String, Number> grafico = new LineChart<>(ejeDias, ejeIngresos);
        grafico.getData().add(serie);
        grafico.setTitle(ventas.isEmpty() ? getTitulo() + " (sin datos en el periodo)" : getTitulo());
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        return grafico;
    }
}
