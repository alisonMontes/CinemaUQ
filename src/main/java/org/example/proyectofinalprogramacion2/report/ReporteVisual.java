package org.example.proyectofinalprogramacion2.report;

import javafx.scene.chart.Chart;
import org.example.proyectofinalprogramacion2.exception.ReporteException;

import java.time.LocalDate;

/**
 * Reporte que se muestra como gráfico de JavaFX.
 */
// ISP: separada de ReporteExportable; un gráfico no necesita generar texto ni un reporte TXT dibujar.
public interface ReporteVisual {

    String getTitulo();

    /**
     * Crea el gráfico con los datos del periodo (inclusive).
     *
     * @throws ReporteException si el periodo no es válido
     */
    Chart crearGrafico(LocalDate desde, LocalDate hasta) throws ReporteException;

    /**
     * Validación común del periodo para las implementaciones.
     */
    static void validarPeriodo(LocalDate desde, LocalDate hasta) throws ReporteException {
        if (desde == null || hasta == null) {
            throw new ReporteException("El periodo es obligatorio");
        }
        if (desde.isAfter(hasta)) {
            throw new ReporteException("La fecha inicial no puede ser posterior a la fecha final");
        }
    }
}
