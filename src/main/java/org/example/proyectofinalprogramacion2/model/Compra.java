package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.adapter.MedioPago;
import org.example.proyectofinalprogramacion2.decorator.PromocionDecorator;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.exception.CompraInvalidaException;
import org.example.proyectofinalprogramacion2.exception.PromocionNoVigenteException;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Compra de un cliente. Se construye con Compra.Builder y su ciclo de vida lo manejan
 * los estados (CompraPendiente, CompraPagada, CompraReembolsada).
 * Si se paga con tarjeta incluye al menos una entrada de una función.
 * Si es una redención de puntos (pagadaConPuntos), la función solo existe cuando se canjea una entrada.
 * Patrones: Builder (Compra.Builder) para crearla y State (EstadoCompraState) para su ciclo de vida.
 */
public class Compra {

    private final String codigo;
    private final Cliente cliente;
    private final Funcion funcion;
    private final List<Entrada> entradas;
    private final List<Producto> productos;
    private final List<Combo> combos;
    private final Promocion promocion;
    private final double total;
    private final LocalDateTime fecha;
    private final boolean pagadaConPuntos;
    private EstadoCompraState estado;
    private MedioPago medioPago;
    private int puntosUsados;
    private double montoReembolsado;

    private Compra(Builder builder, double total) {
        this.codigo = builder.codigo;
        this.cliente = builder.cliente;
        this.funcion = builder.funcion;
        this.entradas = new ArrayList<>(builder.entradas);
        this.productos = new ArrayList<>(builder.productos);
        this.combos = new ArrayList<>(builder.combos);
        this.promocion = builder.promocion;
        this.fecha = builder.fecha;
        this.pagadaConPuntos = builder.pagadaConPuntos;
        this.total = total;
        this.estado = new CompraPendiente();
    }

    // ---------- Transiciones (State) ----------

    /**
     * Marca la compra como pagada con el medio de pago indicado (al que irá un eventual reembolso).
     *
     * @throws CinemaUQException si la compra no está PENDIENTE
     */
    public void confirmarPago(MedioPago medioPago) throws CinemaUQException {
        if (pagadaConPuntos) {
            throw new IllegalStateException("Una redención de puntos no se paga con un medio de pago");
        }
        Objects.requireNonNull(medioPago, "El medio de pago es obligatorio");
        estado.pagar(this);
        this.medioPago = medioPago;
    }

    /**
     * Marca como pagada una redención, registrando los puntos usados.
     *
     * @throws CinemaUQException si la compra no está PENDIENTE
     */
    public void confirmarCanje(int puntosUsados) throws CinemaUQException {
        if (!pagadaConPuntos) {
            throw new IllegalStateException("Solo una redención se paga con puntos");
        }
        Validador.positivo(puntosUsados, "puntos usados");
        estado.pagar(this);
        this.puntosUsados = puntosUsados;
    }

    /**
     * Reembolsa la compra según la estrategia y devuelve el valor a devolver.
     * El estado actual decide si se puede: solo una compra PAGADA con tarjeta se reembolsa.
     *
     * @throws CinemaUQException si el estado o la estrategia no permiten el reembolso
     */
    public double reembolsar(ReembolsoStrategy estrategia) throws CinemaUQException {
        Objects.requireNonNull(estrategia, "La estrategia de reembolso es obligatoria");
        return estado.reembolsar(this, estrategia);
    }

    void cambiarEstado(EstadoCompraState nuevoEstado) {
        this.estado = nuevoEstado;
    }

    void setMontoReembolsado(double montoReembolsado) {
        this.montoReembolsado = Validador.noNegativo(montoReembolsado, "monto reembolsado");
    }

    // ---------- Consultas ----------

    /**
     * Todos los ítems de la compra tratados de forma polimórfica.
     */
    public List<ItemVendible> getItems() {
        List<ItemVendible> items = new ArrayList<>(entradas);
        items.addAll(productos);
        items.addAll(combos);
        return items;
    }

    /**
     * Suma de los precios de los ítems antes de aplicar promociones.
     */
    public double calcularSubtotal() {
        double subtotal = 0;
        for (ItemVendible item : getItems()) {
            subtotal += item.getPrecio();
        }
        return subtotal;
    }

    /** Valor descontado por la promoción (0 en los canjes con puntos). */
    public double getDescuento() {
        return pagadaConPuntos ? 0 : calcularSubtotal() - total;
    }

    public EstadoCompra getEstado() {
        return estado.getValor();
    }

    public String getCodigo() {
        return codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public List<Entrada> getEntradas() {
        return Collections.unmodifiableList(entradas);
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public List<Combo> getCombos() {
        return Collections.unmodifiableList(combos);
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public double getTotal() {
        return total;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean isPagadaConPuntos() {
        return pagadaConPuntos;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public int getPuntosUsados() {
        return puntosUsados;
    }

    public double getMontoReembolsado() {
        return montoReembolsado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Compra)) return false;
        return codigo.equals(((Compra) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Compra{codigo='" + codigo + "', cliente=" + cliente.getCorreo()
                + ", funcion=" + (funcion == null ? "-" : funcion.getCodigo())
                + ", items=" + getItems().size() + ", total=" + total + ", fecha=" + fecha
                + ", estado=" + getEstado() + ", pagadaConPuntos=" + pagadaConPuntos + "}";
    }

    /**
     * Builder: arma la compra paso a paso. build() valida, calcula el total
     * (aplicando la promoción con PromocionDecorator) y deja la compra PENDIENTE.
     */
    // SRP: solo arma y valida la compra; no cobra ni ocupa asientos.
    public static class Builder {

        private final String codigo;
        private final Cliente cliente;
        private final LocalDateTime fecha;
        private Funcion funcion;
        private final List<Entrada> entradas = new ArrayList<>();
        private final List<Producto> productos = new ArrayList<>();
        private final List<Combo> combos = new ArrayList<>();
        private Promocion promocion;
        private boolean pagadaConPuntos;

        public Builder(String codigo, Cliente cliente, LocalDateTime fecha) {
            this.codigo = Validador.texto(codigo, "código");
            this.cliente = Validador.noNulo(cliente, "cliente");
            this.fecha = Validador.noNulo(fecha, "fecha");
        }

        public Builder funcion(Funcion funcion) {
            this.funcion = funcion;
            return this;
        }

        public Builder agregarEntrada(Entrada entrada) {
            entradas.add(Validador.noNulo(entrada, "entrada"));
            return this;
        }

        public Builder agregarProducto(Producto producto) {
            productos.add(Validador.noNulo(producto, "producto"));
            return this;
        }

        public Builder agregarCombo(Combo combo) {
            combos.add(Validador.noNulo(combo, "combo"));
            return this;
        }

        public Builder promocion(Promocion promocion) {
            this.promocion = promocion;
            return this;
        }

        public Builder pagadaConPuntos(boolean pagadaConPuntos) {
            this.pagadaConPuntos = pagadaConPuntos;
            return this;
        }

        /**
         * Valida la compra y la crea en estado PENDIENTE con su total calculado.
         *
         * @throws CompraInvalidaException si no tiene entradas, mezcla funciones o el canje no tiene un solo beneficio
         * @throws PromocionNoVigenteException si la promoción no está vigente en la fecha de la compra
         */
        public Compra build() throws CompraInvalidaException, PromocionNoVigenteException {
            // Todas las entradas deben ser de la función de la compra.
            for (Entrada entrada : entradas) {
                if (!entrada.getFuncion().equals(funcion)) {
                    throw new CompraInvalidaException("La entrada " + entrada.getAsiento().getCodigo()
                            + " no corresponde a la función de la compra");
                }
            }
            if (pagadaConPuntos) {
                validarRedencion();
            } else {
                validarCompraConTarjeta();
            }
            return new Compra(this, calcularTotal());
        }

        private void validarCompraConTarjeta() throws CompraInvalidaException, PromocionNoVigenteException {
            if (funcion == null || entradas.isEmpty()) {
                throw new CompraInvalidaException("Toda compra debe incluir al menos una entrada de una función");
            }
            if (promocion != null && !promocion.estaVigente(fecha.toLocalDate())) {
                throw new PromocionNoVigenteException("La promoción " + promocion.getNombre() + " no está vigente");
            }
        }

        private void validarRedencion() throws CompraInvalidaException {
            if (entradas.size() + productos.size() + combos.size() != 1) {
                throw new CompraInvalidaException("Una redención canjea exactamente un beneficio");
            }
            if (promocion != null) {
                throw new CompraInvalidaException("Una redención de puntos no admite promociones");
            }
        }

        private double calcularTotal() {
            if (pagadaConPuntos) {
                return 0;
            }
            List<ItemVendible> items = new ArrayList<>(entradas);
            items.addAll(productos);
            items.addAll(combos);
            double total = 0;
            for (ItemVendible item : items) {
                // Decorator: si hay promoción, cada ítem se envuelve para aplicarle el descuento.
                ItemVendible conPromocion = promocion == null
                        ? item
                        : new PromocionDecorator(item, promocion, fecha.toLocalDate());
                total += conPromocion.getPrecio();
            }
            return total;
        }
    }
}
