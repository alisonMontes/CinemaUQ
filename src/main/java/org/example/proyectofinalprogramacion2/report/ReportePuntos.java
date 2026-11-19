package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.ItemVendible;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Puntos acumulados y redimidos en el periodo. Los canjes de producto o combo no tienen función
 * y sí se cuentan como puntos redimidos.
 * Patrón Template Method: subclase de ReporteTxt.
 */
// SRP: solo presenta los movimientos de puntos que consulta EstadisticaService.
public class ReportePuntos extends ReporteTxt {

    public ReportePuntos(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de puntos acumulados y redimidos";
    }

    @Override
    protected String getNota() {
        return "acumulaciones por fecha de obtención y canjes por fecha del canje.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        // Acumulaciones y canjes juntos, en orden de fecha (el orden es estable: a igual fecha, acumulaciones primero).
        Map<String[], LocalDate> filas = new LinkedHashMap<>();
        for (Map.Entry<Cliente, List<AcumulacionPuntos>> fila : estadisticas.getAcumulacionesPuntos(desde, hasta)
                .entrySet()) {
            for (AcumulacionPuntos acumulacion : fila.getValue()) {
                filas.put(new String[]{FormatoReporte.fecha(acumulacion.getFechaObtencion()), fila.getKey().getNombre(),
                        "Acumulación", "+" + acumulacion.getPuntosIniciales(), detalle(acumulacion)},
                        acumulacion.getFechaObtencion());
            }
        }
        for (Compra canje : estadisticas.getRedenciones(desde, hasta)) {
            filas.put(new String[]{FormatoReporte.fecha(canje.getFecha().toLocalDate()), canje.getCliente().getNombre(),
                    "Redención", "-" + canje.getPuntosUsados(), "Canje " + canje.getCodigo() + ": " + items(canje)},
                    canje.getFecha().toLocalDate());
        }
        List<Map.Entry<String[], LocalDate>> ordenadas = new ArrayList<>(filas.entrySet());
        ordenadas.sort(Map.Entry.comparingByValue());

        TablaReporte tabla = new TablaReporte("Fecha", "Cliente", "Movimiento", "Puntos", "Detalle")
                .alinearDerecha(3);
        for (Map.Entry<String[], LocalDate> fila : ordenadas) {
            tabla.agregarFila(fila.getKey());
        }
        return tabla;
    }

    private String detalle(AcumulacionPuntos acumulacion) {
        String origen = acumulacion.getCompraOrigen() == null
                ? "Sin compra asociada"
                : "Compra " + acumulacion.getCompraOrigen().getCodigo();
        String estado = acumulacion.isAnulada()
                ? "ANULADA por reembolso"
                : "vence " + FormatoReporte.fecha(acumulacion.getFechaVencimiento());
        return origen + ", " + estado;
    }

    private String items(Compra compra) {
        StringJoiner nombres = new StringJoiner(", ");
        for (ItemVendible item : compra.getItems()) {
            nombres.add(item.getNombre());
        }
        return nombres.toString();
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Puntos acumulados (sin anulados)", String.valueOf(estadisticas.getPuntosAcumulados(desde, hasta)));
        totales.put("Puntos redimidos", String.valueOf(estadisticas.getPuntosRedimidos(desde, hasta)));
        return totales;
    }
}
