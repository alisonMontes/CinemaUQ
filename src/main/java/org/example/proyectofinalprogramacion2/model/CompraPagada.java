package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.CancelacionNoPermitidaException;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

/**
 * Compra pagada (con tarjeta o con puntos). Al reembolsar pasa a REEMBOLSADA.
 * Las compras pagadas con puntos no se reembolsan.
 */
// SRP: solo define qué se puede hacer con una compra pagada.
public class CompraPagada implements EstadoCompraState {

    @Override
    public EstadoCompra getValor() {
        return EstadoCompra.PAGADA;
    }

    @Override
    public void pagar(Compra compra) throws CinemaUQException {
        throw new CinemaUQException("La compra " + compra.getCodigo() + " ya fue pagada");
    }

    @Override
    public double reembolsar(Compra compra, ReembolsoStrategy estrategia) throws CinemaUQException {
        if (compra.isPagadaConPuntos()) {
            throw new CancelacionNoPermitidaException("Las compras pagadas con puntos no se pueden cancelar");
        }
        double monto = estrategia.calcularReembolso(compra.getTotal());
        compra.setMontoReembolsado(monto);
        compra.cambiarEstado(new CompraReembolsada());
        return monto;
    }
}
