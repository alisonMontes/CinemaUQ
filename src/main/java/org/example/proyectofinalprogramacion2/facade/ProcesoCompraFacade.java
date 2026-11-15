package org.example.proyectofinalprogramacion2.facade;

import org.example.proyectofinalprogramacion2.adapter.MedioPago;
import org.example.proyectofinalprogramacion2.exception.AsientoNoDisponibleException;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.exception.FuncionNoDisponibleException;
import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Compra;
import org.example.proyectofinalprogramacion2.model.ConfiguracionPuntos;
import org.example.proyectofinalprogramacion2.model.Entrada;
import org.example.proyectofinalprogramacion2.model.EstadoCompra;
import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;
import org.example.proyectofinalprogramacion2.strategy.SelectorReembolso;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Facade: ofrece operaciones simples (comprar, cancelar compra, cancelar función) y oculta
 * la coordinación entre función, asientos, medio de pago, estados de la compra, puntos y notificaciones.
 */
// SRP: solo coordina los pasos del proceso; cada paso lo ejecuta la clase responsable.
// DIP: depende de MedioPago y ReembolsoStrategy (interfaces), no de TarjetaVirtual ni de estrategias concretas.
public class ProcesoCompraFacade {

    private final CinemaUQ cinema;
    private final SelectorReembolso selectorReembolso;

    public ProcesoCompraFacade() {
        this(CinemaUQ.getInstancia(), new SelectorReembolso());
    }

    public ProcesoCompraFacade(CinemaUQ cinema, SelectorReembolso selectorReembolso) {
        this.cinema = Objects.requireNonNull(cinema);
        this.selectorReembolso = Objects.requireNonNull(selectorReembolso);
    }

    /**
     * Paga una compra PENDIENTE con el medio de pago indicado.
     * Pasos: validar función y asientos, validar el medio de pago, pasar la compra a PAGADA,
     * cobrar (registra el movimiento), ocupar los asientos, acumular puntos, registrar la compra
     * y suscribir al cliente a la función.
     */
    public Compra comprar(Compra compra, MedioPago medioPago, LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(compra, "La compra es obligatoria");
        Objects.requireNonNull(medioPago, "El medio de pago es obligatorio");
        Objects.requireNonNull(ahora, "La fecha actual es obligatoria");
        if (compra.isPagadaConPuntos()) {
            throw new CinemaUQException("Una redención de puntos no se paga con tarjeta");
        }

        Funcion funcion = compra.getFuncion();
        validarFuncionDisponible(funcion, ahora);
        validarAsientos(compra);
        medioPago.validarPago(compra.getTotal());

        compra.confirmarPago(medioPago);
        medioPago.cobrar(compra.getTotal(), "Pago compra " + compra.getCodigo(), ahora);
        for (Entrada entrada : compra.getEntradas()) {
            funcion.ocuparAsiento(entrada.getAsiento());
        }
        acumularPuntos(compra, ahora);

        Cliente cliente = compra.getCliente();
        cliente.agregarCompra(compra);
        cinema.agregarCompra(compra);
        funcion.agregarObservador(cliente);
        return compra;
    }

    /**
     * El cliente cancela una compra. La estrategia se elige según el tiempo que falta para la función.
     *
     * @return valor reembolsado a la tarjeta con la que se pagó
     */
    public double cancelarCompra(Compra compra, LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(compra, "La compra es obligatoria");
        ReembolsoStrategy estrategia = selectorReembolso.seleccionar(compra.getFuncion(), ahora);
        return reembolsar(compra, estrategia, ahora);
    }

    /**
     * El cine cancela una función: avisa a los clientes que la observan y reembolsa el 100 %
     * de cada compra PAGADA con tarjeta.
     *
     * @return compras reembolsadas
     */
    public List<Compra> cancelarFuncion(Funcion funcion, LocalDateTime ahora) throws CinemaUQException {
        Objects.requireNonNull(funcion, "La función es obligatoria");
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            throw new FuncionNoDisponibleException("La función " + funcion.getCodigo() + " ya está cancelada");
        }
        funcion.cancelar();

        List<Compra> reembolsadas = new ArrayList<>();
        ReembolsoStrategy estrategia = selectorReembolso.seleccionar(funcion, ahora);
        for (Compra compra : new ArrayList<>(cinema.getCompras())) {
            if (funcion.equals(compra.getFuncion()) && compra.getEstado() == EstadoCompra.PAGADA
                    && !compra.isPagadaConPuntos()) {
                reembolsar(compra, estrategia, ahora);
                reembolsadas.add(compra);
            }
        }
        return reembolsadas;
    }

    // ---------- Pasos internos ----------

    private double reembolsar(Compra compra, ReembolsoStrategy estrategia, LocalDateTime ahora)
            throws CinemaUQException {
        double monto = compra.reembolsar(estrategia);
        if (monto > 0) {
            compra.getMedioPago().reembolsar(monto, "Reembolso compra " + compra.getCodigo()
                    + " - " + estrategia.getDescripcion(), ahora);
        }
        Funcion funcion = compra.getFuncion();
        if (funcion.getEstado() != EstadoFuncion.CANCELADA) {
            for (Entrada entrada : compra.getEntradas()) {
                funcion.liberarAsiento(entrada.getAsiento());
            }
        }
        Cliente cliente = compra.getCliente();
        cliente.getCuentaPuntos().anularPuntosDe(compra);
        if (!cliente.tieneCompraPagadaEn(funcion)) {
            funcion.eliminarObservador(cliente);
        }
        return monto;
    }

    private void validarFuncionDisponible(Funcion funcion, LocalDateTime ahora) throws FuncionNoDisponibleException {
        if (!funcion.estaDisponibleParaVenta(ahora)) {
            throw new FuncionNoDisponibleException("La función " + funcion.getCodigo()
                    + " no está disponible (" + funcion.getEstado() + " o ya comenzó)");
        }
    }

    private void validarAsientos(Compra compra) throws AsientoNoDisponibleException {
        Funcion funcion = compra.getFuncion();
        Set<Asiento> elegidos = new HashSet<>();
        for (Entrada entrada : compra.getEntradas()) {
            Asiento asiento = entrada.getAsiento();
            if (!elegidos.add(asiento) || !funcion.estaDisponible(asiento)) {
                throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo()
                        + " no está disponible en la función " + funcion.getCodigo());
            }
        }
    }

    private void acumularPuntos(Compra compra, LocalDateTime ahora) {
        int puntos = ConfiguracionPuntos.calcularPuntos(compra.getTotal());
        if (puntos > 0) {
            compra.getCliente().getCuentaPuntos()
                    .agregarAcumulacion(new AcumulacionPuntos(puntos, ahora.toLocalDate(), compra));
        }
    }
}
