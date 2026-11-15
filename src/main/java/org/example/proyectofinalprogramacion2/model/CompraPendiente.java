package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.CancelacionNoPermitidaException;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

/**
 * Compra armada pero sin pagar. Al pagar pasa a PAGADA.
 */
// SRP: solo define qué se puede hacer con una compra pendiente.
public class CompraPendiente implements EstadoCompraState {

    @Override
    public EstadoCompra getValor() {
        return EstadoCompra.PENDIENTE;
    }

    @Override
    public void pagar(Compra compra) {
        compra.cambiarEstado(new CompraPagada());
    }

    @Override
    public double reembolsar(Compra compra, ReembolsoStrategy estrategia) throws CancelacionNoPermitidaException {
        throw new CancelacionNoPermitidaException("La compra " + compra.getCodigo() + " aún no ha sido pagada");
    }
}
