package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Productos y combos vendidos en el periodo, incluidos los canjeados con puntos (compras sin función).
 * Patrón Template Method: subclase de ReporteTxt.
 */
// SRP: solo presenta las unidades de confitería que calcula EstadisticaService.
public class ReporteProductosCombos extends ReporteTxt {

    public ReporteProductosCombos(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de productos y combos vendidos";
    }

    @Override
    protected String getNota() {
        return "compras pagadas en el periodo, incluidos los canjes con puntos;"
                + " los productos que van dentro de un combo no se cuentan por separado.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Tipo", "Código", "Nombre", "Unidades").alinearDerecha(3);
        for (Map.Entry<Producto, Integer> fila : estadisticas.getProductosVendidos(desde, hasta).entrySet()) {
            tabla.agregarFila("Producto", fila.getKey().getCodigo(), fila.getKey().getNombre(),
                    String.valueOf(fila.getValue()));
        }
        for (Map.Entry<Combo, Integer> fila : estadisticas.getCombosVendidos(desde, hasta).entrySet()) {
            tabla.agregarFila("Combo", fila.getKey().getCodigo(), fila.getKey().getNombre(),
                    String.valueOf(fila.getValue()));
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total productos", String.valueOf(sumar(estadisticas.getProductosVendidos(desde, hasta))));
        totales.put("Total combos", String.valueOf(sumar(estadisticas.getCombosVendidos(desde, hasta))));
        return totales;
    }

    private static int sumar(Map<?, Integer> unidades) {
        int total = 0;
        for (int cantidad : unidades.values()) {
            total += cantidad;
        }
        return total;
    }
}
