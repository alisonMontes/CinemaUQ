package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.adapter.PagoTarjetaAdapter;
import org.example.proyectofinalprogramacion2.exception.AsientoNoDisponibleException;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.exception.FuncionNoDisponibleException;
import org.example.proyectofinalprogramacion2.facade.ProcesoCompraFacade;
import org.example.proyectofinalprogramacion2.factory.EntradaFactory;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.ConfiguracionPuntos;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Promocion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Compras con tarjeta y redenciones con puntos.
 * La compra se arma con Compra.Builder y se paga con ProcesoCompraFacade;
 * la fábrica de cada entrada se elige según el tipo de asiento.
 */
// SRP: solo arma compras y canjes; el cobro lo hace la fachada y el descuento de puntos PuntosService.
public class CompraService {

    private final CinemaUQ cinema;
    private final ProcesoCompraFacade procesoCompra;
    private final PuntosService puntosService;

    public CompraService() {
        this(CinemaUQ.getInstancia(), new ProcesoCompraFacade(), new PuntosService());
    }

    public CompraService(CinemaUQ cinema, ProcesoCompraFacade procesoCompra, PuntosService puntosService) {
        this.cinema = cinema;
        this.procesoCompra = procesoCompra;
        this.puntosService = puntosService;
    }

    /**
     * Compra entradas (y opcionalmente productos, combos y una promoción vigente) pagando con la
     * tarjeta del cliente. Al pagar, el cliente queda como observador de la función.
     *
     * @param promocion puede ser null
     */
    public Compra comprarEntradas(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                                  List<Producto> productos, List<Combo> combos, Promocion promocion,
                                  LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(cliente, "El cliente es obligatorio");
        Objects.requireNonNull(funcion, "La función es obligatoria");
        exigirFuncionDisponible(funcion, ahora);

        Compra compra = armarCompra(cinema.generarCodigoCompra(), cliente, funcion, asientos, productos, combos,
                promocion, ahora);
        return procesoCompra.comprar(compra, new PagoTarjetaAdapter(cliente.getTarjeta()), ahora);
    }

    /**
     * Calcula el total que se cobraría (con la promoción, si se indica) sin registrar la compra,
     * ocupar asientos ni cobrar. Sirve para mostrar subtotales antes de pagar.
     *
     * @param promocion puede ser null
     */
    public double calcularTotal(Cliente cliente, Funcion funcion, List<Asiento> asientos,
                                List<Producto> productos, List<Combo> combos, Promocion promocion,
                                LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(cliente, "El cliente es obligatorio");
        Objects.requireNonNull(funcion, "La función es obligatoria");
        return armarCompra("COTIZACION", cliente, funcion, asientos, productos, combos, promocion, ahora)
                .getTotal();
    }

    private Compra armarCompra(String codigo, Cliente cliente, Funcion funcion, List<Asiento> asientos,
                               List<Producto> productos, List<Combo> combos, Promocion promocion,
                               LocalDateTime ahora) throws CinemaUQException {
        Compra.Builder builder = new Compra.Builder(codigo, cliente, ahora)
                .funcion(funcion)
                .promocion(promocion);
        if (asientos != null) {
            for (Asiento asiento : asientos) {
                builder.agregarEntrada(EntradaFactory.segunAsiento(asiento).crearEntrada(funcion, asiento));
            }
        }
        if (productos != null) {
            for (Producto producto : productos) {
                builder.agregarProducto(producto);
            }
        }
        if (combos != null) {
            for (Combo combo : combos) {
                builder.agregarCombo(combo);
            }
        }
        return builder.build();
    }

    // ---------- Redención con puntos ----------

    /**
     * Canjea una entrada (con su asiento) por ConfiguracionPuntos.PUNTOS_ENTRADA puntos.
     */
    public Compra redimirEntrada(Cliente cliente, Funcion funcion, Asiento asiento, LocalDateTime ahora)
            throws CinemaUQException {
        Objects.requireNonNull(funcion, "La función es obligatoria");
        Objects.requireNonNull(asiento, "El asiento es obligatorio");
        exigirFuncionDisponible(funcion, ahora);
        if (!funcion.estaDisponible(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo()
                    + " no está disponible en la función " + funcion.getCodigo());
        }
        Compra.Builder builder = nuevaRedencion(cliente, ahora)
                .funcion(funcion)
                .agregarEntrada(EntradaFactory.segunAsiento(asiento).crearEntrada(funcion, asiento));
        Compra compra = canjear(builder, ConfiguracionPuntos.PUNTOS_ENTRADA, ahora);
        funcion.ocuparAsiento(asiento);
        funcion.agregarObservador(cliente);
        return compra;
    }

    /**
     * Canjea un producto por ConfiguracionPuntos.PUNTOS_PRODUCTO puntos (compra sin función).
     */
    public Compra redimirProducto(Cliente cliente, Producto producto, LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(producto, "El producto es obligatorio");
        return canjear(nuevaRedencion(cliente, ahora).agregarProducto(producto),
                ConfiguracionPuntos.PUNTOS_PRODUCTO, ahora);
    }

    /**
     * Canjea un combo por ConfiguracionPuntos.PUNTOS_COMBO puntos (compra sin función).
     */
    public Compra redimirCombo(Cliente cliente, Combo combo, LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(combo, "El combo es obligatorio");
        return canjear(nuevaRedencion(cliente, ahora).agregarCombo(combo),
                ConfiguracionPuntos.PUNTOS_COMBO, ahora);
    }

    private Compra.Builder nuevaRedencion(Cliente cliente, LocalDateTime ahora) {
        Objects.requireNonNull(cliente, "El cliente es obligatorio");
        return new Compra.Builder(cinema.generarCodigoCompra(), cliente, ahora).pagadaConPuntos(true);
    }

    /**
     * Valida la redención, descuenta los puntos y registra la compra pagada con puntos.
     * No pasa por la tarjeta y no genera puntos.
     */
    private Compra canjear(Compra.Builder builder, int puntos, LocalDateTime ahora) throws CinemaUQException {
        Compra compra = builder.build();
        Cliente cliente = compra.getCliente();
        puntosService.redimir(cliente, puntos, ahora.toLocalDate());
        compra.confirmarCanje(puntos);
        cliente.agregarCompra(compra);
        cinema.agregarCompra(compra);
        return compra;
    }

    private void exigirFuncionDisponible(Funcion funcion, LocalDateTime ahora) throws FuncionNoDisponibleException {
        if (!funcion.estaDisponibleParaVenta(ahora)) {
            throw new FuncionNoDisponibleException("La función " + funcion.getCodigo()
                    + " no está disponible (" + funcion.getEstado() + " o ya comenzó)");
        }
    }
}
