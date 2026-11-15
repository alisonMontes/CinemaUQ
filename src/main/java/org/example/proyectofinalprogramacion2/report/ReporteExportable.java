package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.exception.ReporteException;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reporte que se puede generar como texto plano para exportarlo a un archivo TXT.
 */
// ISP: solo lo que la interfaz y el exportador necesitan de un reporte.
// DIP: la interfaz y GeneradorReportes dependen de esta abstracción, no de los reportes concretos.
public interface ReporteExportable {

    String getTitulo();

    /**
     * Genera el texto del reporte para el periodo indicado (inclusive).
     *
     * @param ahora fecha y hora de generación que se imprime en el encabezado
     * @throws ReporteException si el periodo no es válido
     */
    String generarTexto(LocalDate desde, LocalDate hasta, LocalDateTime ahora) throws ReporteException;
}
