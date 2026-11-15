package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.service.EstadisticaService;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ocupación de las funciones que se proyectan en el periodo y ocupación total de cada sala.
 */
// SRP: solo presenta la ocupación que calcula EstadisticaService.
public class ReporteOcupacionSalas extends ReporteTxt {

    public ReporteOcupacionSalas(EstadisticaService estadisticas) {
        super(estadisticas);
    }

    @Override
    public String getTitulo() {
        return "Reporte de ocupación de salas";
    }

    @Override
    protected String getNota() {
        return "funciones no canceladas cuya fecha de proyección está en el periodo.";
    }

    @Override
    protected TablaReporte construirTabla(LocalDate desde, LocalDate hasta) {
        TablaReporte tabla = new TablaReporte("Función", "Película", "Sala", "Proyección", "Ocupados", "Ocupación")
                .alinearDerecha(4, 5);
        for (Map.Entry<Funcion, Double> fila : estadisticas.getOcupacionPorFuncion(desde, hasta).entrySet()) {
            Funcion funcion = fila.getKey();
            tabla.agregarFila(funcion.getCodigo(), funcion.getPelicula().getTitulo(), funcion.getSala().getNombre(),
                    FormatoReporte.fechaHora(funcion.getFechaHora()),
                    funcion.getAsientosOcupados().size() + "/" + funcion.getSala().getCapacidad(),
                    FormatoReporte.porcentaje(fila.getValue()));
        }
        return tabla;
    }

    @Override
    protected Map<String, String> calcularTotales(LocalDate desde, LocalDate hasta) {
        Map<String, String> totales = new LinkedHashMap<>();
        for (Map.Entry<Sala, Double> sala : estadisticas.getOcupacionPorSala(desde, hasta).entrySet()) {
            totales.put("Ocupación " + sala.getKey().getNombre(), FormatoReporte.porcentaje(sala.getValue()));
        }
        return totales;
    }
}
