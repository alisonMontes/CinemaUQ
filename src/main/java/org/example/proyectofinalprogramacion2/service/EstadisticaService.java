package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Sala;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Estadísticas del cine. Criterios:
 * - Dinero: solo compras pagadas con tarjeta; el ingreso neto es el total menos lo reembolsado.
 * - Unidades (entradas, productos, combos): compras PAGADAS, con tarjeta o con puntos.
 * - Las redenciones de producto o combo no tienen función: se omiten donde se necesita una función.
 */
// SRP: solo calcula estadísticas de lectura; no modifica datos.
public class EstadisticaService {

    private final CinemaUQ cinema;

    public EstadisticaService() {
        this(CinemaUQ.getInstancia());
    }

    public EstadisticaService(CinemaUQ cinema) {
        this.cinema = cinema;
    }

    // ---------- Ventas por periodo ----------

    /**
     * Compras (de cualquier tipo y estado) hechas entre las dos fechas, inclusive.
     */
    public List<Compra> getComprasEnPeriodo(LocalDate desde, LocalDate hasta) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : cinema.getCompras()) {
            LocalDate dia = compra.getFecha().toLocalDate();
            if (!dia.isBefore(desde) && !dia.isAfter(hasta)) {
                resultado.add(compra);
            }
        }
        return resultado;
    }

    /**
     * Ingresos netos (pagado con tarjeta menos reembolsado) del periodo.
     */
    public double getIngresosNetos(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Compra compra : getComprasEnPeriodo(desde, hasta)) {
            total += ingresoNeto(compra);
        }
        return total;
    }

    /**
     * Ingresos netos agrupados por día (ordenados por fecha).
     */
    public Map<LocalDate, Double> getVentasPorDia(LocalDate desde, LocalDate hasta) {
        Map<LocalDate, Double> ventas = new TreeMap<>();
        for (Compra compra : getComprasEnPeriodo(desde, hasta)) {
            if (!compra.isPagadaConPuntos() && compra.getEstado() != EstadoCompra.PENDIENTE) {
                ventas.merge(compra.getFecha().toLocalDate(), ingresoNeto(compra), Double::sum);
            }
        }
        return ventas;
    }

    private double ingresoNeto(Compra compra) {
        if (compra.isPagadaConPuntos() || compra.getEstado() == EstadoCompra.PENDIENTE) {
            return 0;
        }
        return compra.getTotal() - compra.getMontoReembolsado();
    }

    // ---------- Ocupación ----------

    /**
     * Porcentaje de asientos ocupados de cada función no cancelada.
     */
    public Map<Funcion, Double> getOcupacionPorFuncion() {
        Map<Funcion, Double> ocupacion = new LinkedHashMap<>();
        for (Funcion funcion : cinema.getFunciones()) {
            int capacidad = funcion.getSala().getCapacidad();
            if (funcion.getEstado() != EstadoFuncion.CANCELADA && capacidad > 0) {
                ocupacion.put(funcion, funcion.getAsientosOcupados().size() * 100.0 / capacidad);
            }
        }
        return ocupacion;
    }

    /**
     * Porcentaje de ocupación de cada sala: asientos ocupados sobre asientos ofrecidos
     * en sus funciones no canceladas. Una sala sin funciones tiene 0 %.
     */
    public Map<Sala, Double> getOcupacionPorSala() {
        Map<Sala, Double> ocupacion = new LinkedHashMap<>();
        for (Sala sala : cinema.getSalas()) {
            int ocupados = 0;
            int ofrecidos = 0;
            for (Funcion funcion : cinema.getFunciones()) {
                if (funcion.getSala().equals(sala) && funcion.getEstado() != EstadoFuncion.CANCELADA) {
                    ocupados += funcion.getAsientosOcupados().size();
                    ofrecidos += sala.getCapacidad();
                }
            }
            ocupacion.put(sala, ofrecidos == 0 ? 0 : ocupados * 100.0 / ofrecidos);
        }
        return ocupacion;
    }

    // ---------- Más vendidos ----------

    /**
     * Entradas entregadas por película (compras PAGADAS), de mayor a menor.
     */
    public Map<Pelicula, Integer> getEntradasPorPelicula() {
        Map<Pelicula, Integer> conteo = new HashMap<>();
        for (Compra compra : getComprasPagadas()) {
            if (compra.getFuncion() != null && !compra.getEntradas().isEmpty()) {
                conteo.merge(compra.getFuncion().getPelicula(), compra.getEntradas().size(), Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    /**
     * Productos vendidos por unidad (sin contar los que van dentro de combos), de mayor a menor.
     */
    public Map<Producto, Integer> getProductosMasVendidos() {
        Map<Producto, Integer> conteo = new HashMap<>();
        for (Compra compra : getComprasPagadas()) {
            for (Producto producto : compra.getProductos()) {
                conteo.merge(producto, 1, Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    public Map<Combo, Integer> getCombosMasVendidos() {
        Map<Combo, Integer> conteo = new HashMap<>();
        for (Compra compra : getComprasPagadas()) {
            for (Combo combo : compra.getCombos()) {
                conteo.merge(combo, 1, Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    // ---------- Reembolsos y puntos ----------

    public List<Compra> getComprasReembolsadas(LocalDate desde, LocalDate hasta) {
        List<Compra> reembolsadas = new ArrayList<>();
        for (Compra compra : getComprasEnPeriodo(desde, hasta)) {
            if (compra.getEstado() == EstadoCompra.REEMBOLSADA) {
                reembolsadas.add(compra);
            }
        }
        return reembolsadas;
    }

    public double getTotalReembolsado(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (Compra compra : getComprasReembolsadas(desde, hasta)) {
            total += compra.getMontoReembolsado();
        }
        return total;
    }

    public int getPuntosRedimidos(LocalDate desde, LocalDate hasta) {
        int total = 0;
        for (Compra compra : getComprasEnPeriodo(desde, hasta)) {
            if (compra.isPagadaConPuntos() && compra.getEstado() == EstadoCompra.PAGADA) {
                total += compra.getPuntosUsados();
            }
        }
        return total;
    }

    // ---------- Auxiliares ----------

    private List<Compra> getComprasPagadas() {
        List<Compra> pagadas = new ArrayList<>();
        for (Compra compra : cinema.getCompras()) {
            if (compra.getEstado() == EstadoCompra.PAGADA) {
                pagadas.add(compra);
            }
        }
        return pagadas;
    }

    private static <K> Map<K, Integer> ordenarDescendente(Map<K, Integer> conteo) {
        List<Map.Entry<K, Integer>> entradas = new ArrayList<>(conteo.entrySet());
        entradas.sort(Map.Entry.<K, Integer>comparingByValue().reversed());
        Map<K, Integer> ordenado = new LinkedHashMap<>();
        for (Map.Entry<K, Integer> entrada : entradas) {
            ordenado.put(entrada.getKey(), entrada.getValue());
        }
        return ordenado;
    }
}
