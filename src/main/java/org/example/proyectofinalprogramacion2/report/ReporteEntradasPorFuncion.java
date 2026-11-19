package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entradas vendidas por función en el periodo. Los canjes de producto o combo no tienen función y no aparecen.
 * Patrón Template Method: subclase de ReporteTxt.
 */
// SRP: solo presenta el conteo de entradas por función.
public class ReporteEntradasPorFuncion extends ReporteTxt {

    public ReporteEntradasPorFuncion(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de entradas vendidas por función";
    }

    @Override
    protected String getNota() {
        return "compras pagadas en el periodo (con tarjeta o canjeadas con puntos), por fecha de compra.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Función", "Película", "Sala", "Proyección", "Estado", "Entradas")
                .alinearDerecha(5);
        for (Map.Entry<Funcion, Integer> fila : estadisticas.getEntradasPorFuncion(desde, hasta).entrySet()) {
            Funcion funcion = fila.getKey();
            tabla.agregarFila(funcion.getCodigo(), funcion.getPelicula().getTitulo(), funcion.getSala().getNombre(),
                    FormatoReporte.fechaHora(funcion.getFechaHora()), funcion.getEstado().toString(),
                    String.valueOf(fila.getValue()));
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        int total = 0;
        for (int entradas : estadisticas.getEntradasPorFuncion(desde, hasta).values()) {
            total += entradas;
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total entradas", String.valueOf(total));
        return totales;
    }
}
