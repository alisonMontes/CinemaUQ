package org.example.proyectofinalprogramacion2.adapter;

import org.example.proyectofinalprogramacion2.exception.CinemaUQException;

import java.time.LocalDateTime;

/**
 * Interfaz que espera el proceso de compra para cobrar y reembolsar.
 */
// ISP: solo las operaciones de pago que necesita la compra; nada de recargas ni administración.
public interface MedioPago {

    /**
     * Verifica, sin modificar nada, que el pago se pueda realizar.
     */
    void validarPago(double monto) throws CinemaUQException;

    void cobrar(double monto, String concepto, LocalDateTime fecha) throws CinemaUQException;

    void reembolsar(double monto, String concepto, LocalDateTime fecha);

    String getDescripcion();
}
