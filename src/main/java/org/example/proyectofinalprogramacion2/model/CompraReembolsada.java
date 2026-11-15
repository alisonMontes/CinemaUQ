package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.exception.ReembolsoYaEjecutadoException;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

/**
 * Compra ya reembolsada: estado final. No se puede pagar ni reembolsar de nuevo.
 */
// SRP: solo define qué se puede hacer con una compra reembolsada (nada).
public class CompraReembolsada implements EstadoCompraState {

    @Override
    public EstadoCompra getValor() {
        return EstadoCompra.REEMBOLSADA;
    }

    @Override
    public void pagar(Compra compra) throws CinemaUQException {
        throw new CinemaUQException("La compra " + compra.getCodigo() + " ya fue reembolsada");
    }

    @Override
    public double reembolsar(Compra compra, ReembolsoStrategy estrategia) throws ReembolsoYaEjecutadoException {
        throw new ReembolsoYaEjecutadoException("La compra " + compra.getCodigo() + " ya fue reembolsada");
    }
}
