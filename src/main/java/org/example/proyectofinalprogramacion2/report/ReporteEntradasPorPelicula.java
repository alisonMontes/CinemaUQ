package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Entradas vendidas por película en el periodo.
 * Patrón Template Method: subclase de ReporteTxt.
 */
// SRP: solo presenta el conteo de entradas por película.
public class ReporteEntradasPorPelicula extends ReporteTxt {

    public ReporteEntradasPorPelicula(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de entradas vendidas por película";
    }

    @Override
    protected String getNota() {
        return "compras pagadas en el periodo (con tarjeta o canjeadas con puntos), por fecha de compra.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Película", "Clasificación", "Entradas").alinearDerecha(2);
        for (Map.Entry<Pelicula, Integer> fila : estadisticas.getEntradasPorPelicula(desde, hasta).entrySet()) {
            tabla.agregarFila(fila.getKey().getTitulo(), fila.getKey().getClasificacion(),
                    String.valueOf(fila.getValue()));
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        int total = 0;
        for (int entradas : estadisticas.getEntradasPorPelicula(desde, hasta).values()) {
            total += entradas;
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total entradas", String.valueOf(total));
        return totales;
    }
}
