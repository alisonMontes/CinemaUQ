package org.example.proyectofinalprogramacion2.strategy;

import org.example.proyectofinalprogramacion2.model.EstadoFuncion;
import org.example.proyectofinalprogramacion2.model.Funcion;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Elige la política de reembolso según el tiempo que falta para la función
 * o si el cine la canceló.
 * Reglas: más de 24 h, 100 %; entre 2 y 24 h, 80 %; menos de 2 h, sin reembolso.
 */
// SRP: solo decide qué estrategia aplica; el cálculo lo hace cada estrategia.
public class SelectorReembolso {

    public static final Duration LIMITE_REEMBOLSO_COMPLETO = Duration.ofHours(24);
    public static final Duration LIMITE_REEMBOLSO_PARCIAL = Duration.ofHours(2);

    /**
     * Devuelve la estrategia que aplica en el momento indicado.
     * Recibe la fecha y hora actual como parámetro para poder probar los límites sin depender del reloj.
     *
     * @param funcion función de la compra; si es null (canje sin función) no hay reembolso
     * @param ahora   momento en que se pide la cancelación
     * @return ReembolsoCompleto, ReembolsoParcial o SinReembolso
     */
    public ReembolsoStrategy seleccionar(Funcion funcion, LocalDateTime ahora) {
        if (funcion == null) {
            return new SinReembolso();
        }
        // Si el cine canceló la función siempre se devuelve el 100 %.
        if (funcion.getEstado() == EstadoFuncion.CANCELADA) {
            return new ReembolsoCompleto();
        }
        Duration restante = Duration.between(ahora, funcion.getFechaHora());
        if (restante.compareTo(LIMITE_REEMBOLSO_COMPLETO) > 0) {
            return new ReembolsoCompleto();
        }
        if (restante.compareTo(LIMITE_REEMBOLSO_PARCIAL) >= 0) {
            return new ReembolsoParcial();
        }
        return new SinReembolso();
    }
}
