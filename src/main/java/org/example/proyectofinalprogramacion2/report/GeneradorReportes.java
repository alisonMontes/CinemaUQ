package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.util.Objects;

/**
 * Entrega el reporte que corresponde a cada tipo, para que la interfaz trabaje solo con ReporteExportable.
 */
// SRP: solo decide qué clase concreta corresponde a cada tipo de reporte.
// DIP: quien pide un reporte recibe la abstracción ReporteExportable.
public class GeneradorReportes {

    private final EstadisticaService estadisticas;

    public GeneradorReportes() {
        this(new EstadisticaService());
    }

    public GeneradorReportes(EstadisticaService estadisticas) {
        this.estadisticas = Objects.requireNonNull(estadisticas, "El servicio de estadísticas es obligatorio");
    }

    public ReporteExportable obtenerReporte(TipoReporte tipo) {
        Objects.requireNonNull(tipo, "El tipo de reporte es obligatorio");
        return switch (tipo) {
            case VENTAS -> new ReporteVentas(estadisticas);
            case ENTRADAS_POR_PELICULA -> new ReporteEntradasPorPelicula(estadisticas);
            case ENTRADAS_POR_FUNCION -> new ReporteEntradasPorFuncion(estadisticas);
            case OCUPACION_SALAS -> new ReporteOcupacionSalas(estadisticas);
            case PRODUCTOS_COMBOS -> new ReporteProductosCombos(estadisticas);
            case RECARGAS -> new ReporteRecargas(estadisticas);
            case REEMBOLSOS -> new ReporteReembolsos(estadisticas);
            case PUNTOS -> new ReportePuntos(estadisticas);
        };
    }
}
