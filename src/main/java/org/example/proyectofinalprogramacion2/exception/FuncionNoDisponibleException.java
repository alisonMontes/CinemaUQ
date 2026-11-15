package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando la función está cancelada, finalizada o ya empezó.
 */
public class FuncionNoDisponibleException extends CinemaUQException {

    public FuncionNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
