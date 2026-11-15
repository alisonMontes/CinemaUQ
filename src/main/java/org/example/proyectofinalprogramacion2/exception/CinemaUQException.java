package org.example.proyectofinalprogramacion2.exception;

/**
 * Excepción base de todas las reglas de negocio de CinemaUQ.
 */
public class CinemaUQException extends Exception {

    public CinemaUQException(String mensaje) {
        super(mensaje);
    }
}
