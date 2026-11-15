package org.example.proyectofinalprogramacion2.strategy;

/**
 * Devuelve el 80 %: faltan desde 2 hasta 24 horas (inclusive) para la función.
 */
// SRP: solo calcula el reembolso del 80 %.
public class ReembolsoParcial implements ReembolsoStrategy {

    public static final double PORCENTAJE = 80;

    @Override
    public double calcularReembolso(double totalPagado) {
        return totalPagado * PORCENTAJE / 100;
    }

    @Override
    public String getDescripcion() {
        return "Reembolso parcial (80 %)";
    }
}
