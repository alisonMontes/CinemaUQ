package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando se intenta aplicar una promoción fuera de su vigencia.
 */
public class PromocionNoVigenteException extends CinemaUQException {

    public PromocionNoVigenteException(String mensaje) {
        super(mensaje);
    }
}
