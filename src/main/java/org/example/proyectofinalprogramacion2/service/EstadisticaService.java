package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.MovimientoTarjeta;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Sala;
import org.example.proyectofinalprogramacion2.model.TipoMovimiento;

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
 * - Periodos: las compras se filtran por su fecha de compra; las funciones, por su fecha de proyección;
 *   las recargas y acumulaciones de puntos, por su fecha de registro. Siempre inclusive.
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
            if (enPeriodo(compra.getFecha().toLocalDate(), desde, hasta)) {
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
        return ocupacionPorFuncion(cinema.getFunciones());
    }

    /**
     * Porcentaje de asientos ocupados de cada función no cancelada que se proyecta en el periodo.
     */
    public Map<Funcion, Double> getOcupacionPorFuncion(LocalDate desde, LocalDate hasta) {
        return ocupacionPorFuncion(getFuncionesEnPeriodo(desde, hasta));
    }

    /**
     * Porcentaje de ocupación de cada sala: asientos ocupados sobre asientos ofrecidos
     * en sus funciones no canceladas. Una sala sin funciones tiene 0 %.
     */
    public Map<Sala, Double> getOcupacionPorSala() {
        return ocupacionPorSala(cinema.getFunciones());
    }

    /**
     * Ocupación de cada sala considerando solo las funciones que se proyectan en el periodo.
     */
    public Map<Sala, Double> getOcupacionPorSala(LocalDate desde, LocalDate hasta) {
        return ocupacionPorSala(getFuncionesEnPeriodo(desde, hasta));
    }

    /**
     * Funciones (de cualquier estado) cuya fecha de proyección está en el periodo, en el orden registrado.
     */
    public List<Funcion> getFuncionesEnPeriodo(LocalDate desde, LocalDate hasta) {
        List<Funcion> funciones = new ArrayList<>();
        for (Funcion funcion : cinema.getFunciones()) {
            if (enPeriodo(funcion.getFecha(), desde, hasta)) {
                funciones.add(funcion);
            }
        }
        return funciones;
    }

    private Map<Funcion, Double> ocupacionPorFuncion(List<Funcion> funciones) {
        Map<Funcion, Double> ocupacion = new LinkedHashMap<>();
        for (Funcion funcion : funciones) {
            int capacidad = funcion.getSala().getCapacidad();
            if (funcion.getEstado() != EstadoFuncion.CANCELADA && capacidad > 0) {
                ocupacion.put(funcion, funcion.getAsientosOcupados().size() * 100.0 / capacidad);
            }
        }
        return ocupacion;
    }

    private Map<Sala, Double> ocupacionPorSala(List<Funcion> funciones) {
        Map<Sala, Double> ocupacion = new LinkedHashMap<>();
        for (Sala sala : cinema.getSalas()) {
            int ocupados = 0;
            int ofrecidos = 0;
            for (Funcion funcion : funciones) {
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
        return contarEntradasPorPelicula(getComprasPagadas(cinema.getCompras()));
    }

    /**
     * Entradas entregadas por película en las compras PAGADAS del periodo, de mayor a menor.
     */
    public Map<Pelicula, Integer> getEntradasPorPelicula(LocalDate desde, LocalDate hasta) {
        return contarEntradasPorPelicula(getComprasPagadas(getComprasEnPeriodo(desde, hasta)));
    }

    /**
     * Entradas entregadas por función en las compras PAGADAS del periodo, de mayor a menor.
     */
    public Map<Funcion, Integer> getEntradasPorFuncion(LocalDate desde, LocalDate hasta) {
        Map<Funcion, Integer> conteo = new HashMap<>();
        for (Compra compra : getComprasPagadas(getComprasEnPeriodo(desde, hasta))) {
            if (compra.getFuncion() != null && !compra.getEntradas().isEmpty()) {
                conteo.merge(compra.getFuncion(), compra.getEntradas().size(), Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    /**
     * Productos vendidos por unidad (sin contar los que van dentro de combos), de mayor a menor.
     */
    public Map<Producto, Integer> getProductosMasVendidos() {
        return contarProductos(getComprasPagadas(cinema.getCompras()));
    }

    /**
     * Productos entregados en las compras PAGADAS del periodo (incluye canjes con puntos), de mayor a menor.
     */
    public Map<Producto, Integer> getProductosVendidos(LocalDate desde, LocalDate hasta) {
        return contarProductos(getComprasPagadas(getComprasEnPeriodo(desde, hasta)));
    }

    public Map<Combo, Integer> getCombosMasVendidos() {
        return contarCombos(getComprasPagadas(cinema.getCompras()));
    }

    /**
     * Combos entregados en las compras PAGADAS del periodo (incluye canjes con puntos), de mayor a menor.
     */
    public Map<Combo, Integer> getCombosVendidos(LocalDate desde, LocalDate hasta) {
        return contarCombos(getComprasPagadas(getComprasEnPeriodo(desde, hasta)));
    }

    private Map<Pelicula, Integer> contarEntradasPorPelicula(List<Compra> compras) {
        Map<Pelicula, Integer> conteo = new HashMap<>();
        for (Compra compra : compras) {
            if (compra.getFuncion() != null && !compra.getEntradas().isEmpty()) {
                conteo.merge(compra.getFuncion().getPelicula(), compra.getEntradas().size(), Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    private Map<Producto, Integer> contarProductos(List<Compra> compras) {
        Map<Producto, Integer> conteo = new HashMap<>();
        for (Compra compra : compras) {
            for (Producto producto : compra.getProductos()) {
                conteo.merge(producto, 1, Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    private Map<Combo, Integer> contarCombos(List<Compra> compras) {
        Map<Combo, Integer> conteo = new HashMap<>();
        for (Compra compra : compras) {
            for (Combo combo : compra.getCombos()) {
                conteo.merge(combo, 1, Integer::sum);
            }
        }
        return ordenarDescendente(conteo);
    }

    // ---------- Reembolsos ----------

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

    // ---------- Recargas ----------

    /**
     * Recargas hechas en el periodo, agrupadas por cliente (solo clientes con recargas).
     */
    public Map<Cliente, List<MovimientoTarjeta>> getRecargas(LocalDate desde, LocalDate hasta) {
        Map<Cliente, List<MovimientoTarjeta>> recargas = new LinkedHashMap<>();
        for (Cliente cliente : cinema.getClientes()) {
            for (MovimientoTarjeta movimiento : cliente.getTarjeta().getMovimientos()) {
                if (movimiento.getTipo() == TipoMovimiento.RECARGA
                        && enPeriodo(movimiento.getFecha().toLocalDate(), desde, hasta)) {
                    recargas.computeIfAbsent(cliente, c -> new ArrayList<>()).add(movimiento);
                }
            }
        }
        return recargas;
    }

    public double getTotalRecargado(LocalDate desde, LocalDate hasta) {
        double total = 0;
        for (List<MovimientoTarjeta> movimientos : getRecargas(desde, hasta).values()) {
            for (MovimientoTarjeta movimiento : movimientos) {
                total += movimiento.getMonto();
            }
        }
        return total;
    }

    // ---------- Puntos ----------

    /**
     * Acumulaciones de puntos obtenidas en el periodo (incluidas las anuladas), agrupadas por cliente.
     */
    public Map<Cliente, List<AcumulacionPuntos>> getAcumulacionesPuntos(LocalDate desde, LocalDate hasta) {
        Map<Cliente, List<AcumulacionPuntos>> acumulaciones = new LinkedHashMap<>();
        for (Cliente cliente : cinema.getClientes()) {
            for (AcumulacionPuntos acumulacion : cliente.getCuentaPuntos().getAcumulaciones()) {
                if (enPeriodo(acumulacion.getFechaObtencion(), desde, hasta)) {
                    acumulaciones.computeIfAbsent(cliente, c -> new ArrayList<>()).add(acumulacion);
                }
            }
        }
        return acumulaciones;
    }

    /**
     * Puntos obtenidos en el periodo, sin contar las acumulaciones anuladas por reembolso.
     */
    public int getPuntosAcumulados(LocalDate desde, LocalDate hasta) {
        int total = 0;
        for (List<AcumulacionPuntos> lista : getAcumulacionesPuntos(desde, hasta).values()) {
            for (AcumulacionPuntos acumulacion : lista) {
                if (!acumulacion.isAnulada()) {
                    total += acumulacion.getPuntosIniciales();
                }
            }
        }
        return total;
    }

    /**
     * Canjes con puntos (compras PAGADAS con puntos) del periodo; pueden tener función o no.
     */
    public List<Compra> getRedenciones(LocalDate desde, LocalDate hasta) {
        List<Compra> redenciones = new ArrayList<>();
        for (Compra compra : getComprasEnPeriodo(desde, hasta)) {
            if (compra.isPagadaConPuntos() && compra.getEstado() == EstadoCompra.PAGADA) {
                redenciones.add(compra);
            }
        }
        return redenciones;
    }

    public int getPuntosRedimidos(LocalDate desde, LocalDate hasta) {
        int total = 0;
        for (Compra compra : getRedenciones(desde, hasta)) {
            total += compra.getPuntosUsados();
        }
        return total;
    }

    // ---------- Auxiliares ----------

    private static boolean enPeriodo(LocalDate dia, LocalDate desde, LocalDate hasta) {
        return !dia.isBefore(desde) && !dia.isAfter(hasta);
    }

    private static List<Compra> getComprasPagadas(List<Compra> compras) {
        List<Compra> pagadas = new ArrayList<>();
        for (Compra compra : compras) {
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
