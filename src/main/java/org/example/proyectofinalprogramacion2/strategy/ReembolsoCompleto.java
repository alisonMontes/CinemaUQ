package org.example.proyectofinalprogramacion2.strategy;

/**
 * Devuelve el 100 %: faltan más de 24 horas o el cine canceló la función.
 * Patrón Strategy: estrategia concreta de reembolso.
 */
// SRP: solo calcula el reembolso del 100 %.
public class ReembolsoCompleto implements ReembolsoStrategy {

    @Override
    public double calcularReembolso(double totalPagado) {
        return totalPagado;
    }

    @Override
    public String getDescripcion() {
        return "Reembolso completo (100 %)";
    }
}
