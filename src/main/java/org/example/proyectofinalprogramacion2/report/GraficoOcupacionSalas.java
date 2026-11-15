package org.example.proyectofinalprogramacion2.report;

import javafx.scene.chart.Chart;
import javafx.scene.chart.PieChart;
import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Gráfico de torta: ocupación de cada sala en las funciones que se proyectan en el periodo.
 * Cada porción es proporcional al porcentaje de ocupación de la sala; las salas en 0 % no se dibujan.
 */
// SRP: solo dibuja la ocupación por sala que calcula EstadisticaService.
public class GraficoOcupacionSalas implements ReporteVisual {

    private final EstadisticaService estadisticas;

    public GraficoOcupacionSalas(EstadisticaService estadisticas) {
        this.estadisticas = Objects.requireNonNull(estadisticas, "El servicio de estadísticas es obligatorio");
    }

    @Override
    public String getTitulo() {
        return "Ocupación de cada sala";
    }

    @Override
    public Chart crearGrafico(LocalDate desde, LocalDate hasta) throws ReporteException {
        ReporteVisual.validarPeriodo(desde, hasta);
        PieChart grafico = new PieChart();
        for (Map.Entry<Sala, Double> sala : estadisticas.getOcupacionPorSala(desde, hasta).entrySet()) {
            if (sala.getValue() > 0) {
                grafico.getData().add(new PieChart.Data(
                        sala.getKey().getNombre() + " (" + FormatoReporte.porcentaje(sala.getValue()) + ")",
                        sala.getValue()));
            }
        }
        grafico.setTitle(grafico.getData().isEmpty() ? getTitulo() + " (sin datos en el periodo)" : getTitulo());
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        return grafico;
    }
}
