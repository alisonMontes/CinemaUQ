package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ventas por periodo: ingresos netos de cada día.
 */
// SRP: solo presenta las ventas por día que calcula EstadisticaService.
public class ReporteVentas extends ReporteTxt {

    public ReporteVentas(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de ventas por periodo";
    }

    @Override
    protected String getNota() {
        return "ingreso neto = pagado con tarjeta menos lo reembolsado; los canjes con puntos no generan ingresos.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Día", "Ingresos netos").alinearDerecha(1);
        for (Map.Entry<LocalDate, Double> venta : estadisticas.getVentasPorDia(desde, hasta).entrySet()) {
            tabla.agregarFila(FormatoReporte.fecha(venta.getKey()), FormatoReporte.dinero(venta.getValue()));
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Reembolsos descontados", FormatoReporte.dinero(estadisticas.getTotalReembolsado(desde, hasta)));
        totales.put("Total ingresos netos", FormatoReporte.dinero(estadisticas.getIngresosNetos(desde, hasta)));
        return totales;
    }
}
