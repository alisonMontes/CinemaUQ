package org.example.proyectofinalprogramacion2.adapter;

import org.example.proyectofinalprogramacion2.exception.CinemaUQException;

import java.time.LocalDateTime;

/**
 * Interfaz que espera el proceso de compra para cobrar y reembolsar.
 * Es la interfaz objetivo del patrón Adapter.
 */
// ISP: solo las operaciones de pago que necesita la compra; nada de recargas ni administración.
public interface MedioPago {

    /**
     * Verifica, sin modificar nada, que el pago se pueda realizar.
     *
     * @throws CinemaUQException si el medio de pago está bloqueado o no tiene saldo suficiente
     */
    void validarPago(double monto) throws CinemaUQException;

    /**
     * Cobra el monto y registra el pago con la fecha indicada.
     *
     * @throws CinemaUQException si el pago no se puede realizar
     */
    void cobrar(double monto, String concepto, LocalDateTime fecha) throws CinemaUQException;

    /** Devuelve el monto al medio de pago y registra el reembolso. */
    void reembolsar(double monto, String concepto, LocalDateTime fecha);

    String getDescripcion();
}
