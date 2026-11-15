package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.exception.ReporteException;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Template Method: generarTexto() fija la estructura de todos los reportes TXT
 * (encabezado, periodo, fecha de generación, tabla, registros encontrados y totales)
 * y cada reporte concreto solo aporta su tabla y sus totales.
 */
// SRP: solo da el formato común; los datos los calcula EstadisticaService.
// OCP: un reporte nuevo es una subclase nueva; esta clase no cambia.
public abstract class ReporteTxt implements ReporteExportable {

    static final String SIN_REGISTROS = "Sin registros en el periodo consultado.";

    private static final int ANCHO_LINEA = 78;

    protected final EstadisticaService estadisticas;

    protected ReporteTxt(EstadisticaService estadisticas) {
        this.estadisticas = Objects.requireNonNull(estadisticas, "El servicio de estadísticas es obligatorio");
    }

    @Override
    public final String generarTexto(LocalDate desde, LocalDate hasta, LocalDateTime ahora) throws ReporteException {
        if (desde == null || hasta == null || ahora == null) {
            throw new ReporteException("El periodo y la fecha de generación son obligatorios");
        }
        if (desde.isAfter(hasta)) {
            throw new ReporteException("La fecha inicial no puede ser posterior a la fecha final");
        }
        TablaReporte tabla = construirTabla(desde, hasta);

        StringBuilder texto = new StringBuilder();
        String doble = "=".repeat(ANCHO_LINEA);
        String simple = "-".repeat(ANCHO_LINEA);
        texto.append(doble).append('\n');
        texto.append("CinemaUQ - ").append(getTitulo().toUpperCase()).append('\n');
        texto.append(doble).append('\n');
        texto.append("Periodo consultado: desde ").append(FormatoReporte.fecha(desde))
                .append(" hasta ").append(FormatoReporte.fecha(hasta)).append('\n');
        texto.append("Fecha de generación: ").append(FormatoReporte.fechaHora(ahora)).append('\n');
        if (getNota() != null) {
            texto.append("Nota: ").append(getNota()).append('\n');
        }
        texto.append(simple).append('\n');

        if (tabla.estaVacia()) {
            texto.append(SIN_REGISTROS).append('\n');
        } else {
            texto.append(tabla.formatear());
        }
        texto.append(simple).append('\n');
        texto.append("Registros encontrados: ").append(tabla.getCantidadFilas()).append('\n');
        if (!tabla.estaVacia()) {
            for (Map.Entry<String, String> total : calcularTotales(desde, hasta).entrySet()) {
                texto.append(total.getKey()).append(": ").append(total.getValue()).append('\n');
            }
        }
        texto.append(doble).append('\n');
        return texto.toString();
    }

    /**
     * Filas del reporte para el periodo.
     */
    protected abstract TablaReporte construirTabla(LocalDate desde, LocalDate hasta);

    /**
     * Totales que se imprimen al final (etiqueta y valor ya formateado), en orden.
     */
    protected abstract Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta);

    /**
     * Aclaración opcional sobre qué incluye el reporte.
     */
    protected String getNota() {
        return null;
    }

    @Override
    public String toString() {
        return getTitulo();
    }
}
