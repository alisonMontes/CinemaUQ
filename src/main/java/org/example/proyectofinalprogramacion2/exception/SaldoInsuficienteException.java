package org.example.proyectofinalprogramacion2.exception;

/**
 * Se lanza cuando la tarjeta no tiene saldo suficiente para el pago.
 */
public class SaldoInsuficienteException extends CinemaUQException {

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
