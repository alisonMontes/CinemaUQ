package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cancelaciones y reembolsos de las compras hechas en el periodo.
 */
// SRP: solo presenta las compras reembolsadas que consulta EstadisticaService.
public class ReporteReembolsos extends ReporteTxt {

    public ReporteReembolsos(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de cancelaciones y reembolsos";
    }

    @Override
    protected String getNota() {
        return "compras hechas en el periodo que quedaron reembolsadas; el valor se devolvió a la tarjeta usada.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Compra", "Cliente", "Función", "Pagado", "Reembolsado", "Motivo")
                .alinearDerecha(3, 4);
        for (Compra compra : estadisticas.getComprasReembolsadas(desde, hasta)) {
            boolean canceladaPorElCine = compra.getFuncion().getEstado() == EstadoFuncion.CANCELADA;
            tabla.agregarFila(compra.getCodigo(), compra.getCliente().getNombre(),
                    compra.getFuncion().getCodigo() + " " + compra.getFuncion().getPelicula().getTitulo(),
                    FormatoReporte.dinero(compra.getTotal()), FormatoReporte.dinero(compra.getMontoReembolsado()),
                    canceladaPorElCine ? "Función cancelada por el cine" : "Cancelada por el cliente");
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        List<Compra> reembolsadas = estadisticas.getComprasReembolsadas(desde, hasta);
        double pagado = 0;
        for (Compra compra : reembolsadas) {
            pagado += compra.getTotal();
        }
        Map<String, String> totales = new LinkedHashMap<>();
        totales.put("Total pagado por estas compras", FormatoReporte.dinero(pagado));
        totales.put("Total reembolsado", FormatoReporte.dinero(estadisticas.getTotalReembolsado(desde, hasta)));
        return totales;
    }
}
