package org.example.proyectofinalprogramacion2.model;

import java.time.LocalDateTime;

/**
 * Registro de una recarga, pago o reembolso sobre una tarjeta virtual.
 */
public class MovimientoTarjeta {

    private TipoMovimiento tipo;
    private LocalDateTime fecha;
    private double monto;
    private String descripcion;

    public MovimientoTarjeta(TipoMovimiento tipo, LocalDateTime fecha, double monto, String descripcion) {
        setTipo(tipo);
        setFecha(fecha);
        setMonto(monto);
        setDescripcion(descripcion);
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = Validador.noNulo(tipo, "tipo de movimiento");
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = Validador.noNulo(fecha, "fecha");
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = Validador.positivo(monto, "monto");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    @Override
    public String toString() {
        return "MovimientoTarjeta{tipo=" + tipo + ", fecha=" + fecha + ", monto=" + monto
                + ", descripcion='" + descripcion + "'}";
    }
}
