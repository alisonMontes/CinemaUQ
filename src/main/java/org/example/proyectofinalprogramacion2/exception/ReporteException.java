package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando un reporte no se puede generar o guardar.
 */
public class ReporteException extends CinemaUQException {

    public ReporteException(String mensaje) {
        super(mensaje);
    }

    public ReporteException(String mensaje, Throwable causa) {
        super(mensaje);
        initCause(causa);
    }
}
