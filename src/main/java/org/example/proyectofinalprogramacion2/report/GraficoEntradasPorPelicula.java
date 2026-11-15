package org.example.proyectofinalprogramacion2.report;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.util.StringConverter;
import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Gráfico de barras: entradas vendidas por película en el periodo (por fecha de compra).
 */
// SRP: solo dibuja las entradas por película que calcula EstadisticaService.
public class GraficoEntradasPorPelicula implements ReporteVisual {

    private final EstadisticaService estadisticas;

    public GraficoEntradasPorPelicula(EstadisticaService estadisticas) {
        this.estadisticas = Objects.requireNonNull(estadisticas, "El servicio de estadísticas es obligatorio");
    }

    @Override
    public String getTitulo() {
        return "Entradas vendidas por película";
    }

    @Override
    public Chart crearGrafico(LocalDate desde, LocalDate hasta) throws ReporteException {
        ReporteVisual.validarPeriodo(desde, hasta);
        Map<Pelicula, Integer> entradas = estadisticas.getEntradasPorPelicula(desde, hasta);

        CategoryAxis ejePeliculas = new CategoryAxis();
        ejePeliculas.setLabel("Película");
        NumberAxis ejeEntradas = new NumberAxis();
        ejeEntradas.setLabel("Entradas");
        ejeEntradas.setMinorTickVisible(false);
        ejeEntradas.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number valor) {
                // Solo se rotulan las marcas enteras: no hay medias entradas.
                return valor.doubleValue() == Math.rint(valor.doubleValue()) ? String.valueOf(valor.intValue()) : "";
            }

            @Override
            public Number fromString(String texto) {
                return Integer.valueOf(texto);
            }
        });

        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Entradas");
        for (Map.Entry<Pelicula, Integer> fila : entradas.entrySet()) {
            serie.getData().add(new XYChart.Data<>(fila.getKey().getTitulo(), fila.getValue()));
        }

        BarChart<String, Number> grafico = new BarChart<>(ejePeliculas, ejeEntradas);
        grafico.getData().add(serie);
        grafico.setTitle(entradas.isEmpty() ? getTitulo() + " (sin datos en el periodo)" : getTitulo());
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        return grafico;
    }
}
