package org.example.proyectofinalprogramacion2.strategy;

import org.example.proyectofinalprogramacion2.exception.CancelacionNoPermitidaException;

/**
 * Strategy: política que define cuánto se devuelve al cancelar una compra.
 */
// ISP: una sola operación de cálculo y su descripción.
// OCP: una nueva política se agrega implementando esta interfaz, sin tocar las existentes.
public interface ReembolsoStrategy {

    /**
     * Valor a devolver sobre el total pagado.
     *
     * @throws CancelacionNoPermitidaException si la política no permite cancelar
     */
    double calcularReembolso(double totalPagado) throws CancelacionNoPermitidaException;

    String getDescripcion();
}
