package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.exception.ReembolsoYaEjecutadoException;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

/**
 * Compra ya reembolsada: estado final. No se puede pagar ni reembolsar de nuevo.
 * Patrón State: estado concreto final de la compra.
 */
// SRP: solo define qué se puede hacer con una compra reembolsada (nada).
public class CompraReembolsada implements EstadoCompraState {

    @Override
    public EstadoCompra getValor() {
        return EstadoCompra.REEMBOLSADA;
    }

    /** Una compra reembolsada no se puede pagar. */
    @Override
    public void pagar(Compra compra) throws CinemaUQException {
        throw new CinemaUQException("La compra " + compra.getCodigo() + " ya fue reembolsada");
    }

    /** El reembolso ya se hizo: lanza ReembolsoYaEjecutadoException. */
    @Override
    public double reembolsar(Compra compra, ReembolsoStrategy estrategia) throws ReembolsoYaEjecutadoException {
        throw new ReembolsoYaEjecutadoException("La compra " + compra.getCodigo() + " ya fue reembolsada");
    }
}
