package org.example.proyectofinalprogramacion2.strategy;

import org.example.proyectofinalprogramacion2.exception.CancelacionNoPermitidaException;

/**
 * No permite cancelar: faltan menos de 2 horas para la función.
 */
// SRP: solo rechaza la cancelación.
public class SinReembolso implements ReembolsoStrategy {

    @Override
    public double calcularReembolso(double totalPagado) throws CancelacionNoPermitidaException {
        throw new CancelacionNoPermitidaException(
                "No se puede cancelar: faltan menos de 2 horas para la función");
    }

    @Override
    public String getDescripcion() {
        return "Cancelación no permitida";
    }
}
