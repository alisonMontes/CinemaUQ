package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Recargas de tarjeta realizadas por el administrador en el periodo.
 */
// SRP: solo presenta las recargas que consulta EstadisticaService.
public class ReporteRecargas extends ReporteTxt {

    public ReporteRecargas(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de recargas realizadas";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Fecha", "Cliente", "Tarjeta", "Monto", "Descripción")
                .alinearDerecha(3);
        for (Map.Entry<Cliente, List<MovimientoTarjeta>> recargas : estadisticas.getRecargas(desde, hasta).entrySet()) {
            Cliente cliente = recargas.getKey();
            for (MovimientoTarjeta recarga : recargas.getValue()) {
                tabla.agregarFila(FormatoReporte.fechaHora(recarga.getFecha()), cliente.getNombre(),
                        cliente.getTarjeta().getNumero(), FormatoReporte.dinero(recarga.getMonto()),
                        recarga.getDescripcion());
            }
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Clientes con recargas", String.valueOf(estadisticas.getRecargas(desde, hasta).size()));
        totales.put("Total recargado", FormatoReporte.dinero(estadisticas.getTotalRecargado(desde, hasta)));
        return totales;
    }
}
