package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.strategy.ReembolsoStrategy;

/**
 * State: comportamiento de una compra según su estado.
 * Cada estado indica su valor del enum EstadoCompra, que es el que se muestra y reporta.
 */
// LSP: Compra trabaja con cualquier estado a través de esta interfaz sin saber cuál es.
public interface EstadoCompraState {

    /** Valor del enum que corresponde a este estado. */
    EstadoCompra getValor();

    /**
     * Pasa la compra a PAGADA si el estado actual lo permite.
     *
     * @throws CinemaUQException si en este estado no se puede pagar
     */
    void pagar(Compra compra) throws CinemaUQException;

    /**
     * Ejecuta el reembolso con la estrategia indicada y devuelve el valor a devolver.
     */
    double reembolsar(Compra compra, ReembolsoStrategy estrategia) throws CinemaUQException;
}
