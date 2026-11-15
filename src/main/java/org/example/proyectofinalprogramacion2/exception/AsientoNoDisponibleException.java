package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando un asiento ya está ocupado en la función.
 */
public class AsientoNoDisponibleException extends CinemaUQException {

    public AsientoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
