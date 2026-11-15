package org.example.proyectofinalprogramacion2.model;

import java.time.LocalDate;

/**
 * Puntos obtenidos en una compra pagada con tarjeta. Vencen un año después de obtenerse.
 */
public class AcumulacionPuntos {

    private int puntosIniciales;
    private int puntosDisponibles;
    private LocalDate fechaObtencion;
    private LocalDate fechaVencimiento;
    private Compra compraOrigen;
    private boolean anulada;

    public AcumulacionPuntos(int puntosIniciales, LocalDate fechaObtencion, Compra compraOrigen) {
        this.puntosIniciales = Validador.positivo(puntosIniciales, "puntos iniciales");
        this.puntosDisponibles = puntosIniciales;
        this.fechaObtencion = Validador.noNulo(fechaObtencion, "fecha de obtención");
        this.fechaVencimiento = fechaObtencion.plusYears(ConfiguracionPuntos.ANIOS_VIGENCIA);
        this.compraOrigen = compraOrigen;
    }

    /**
     * Los puntos se pueden usar hasta el día anterior a la fecha de vencimiento.
     */
    public boolean estaVencida(LocalDate hoy) {
        return !hoy.isBefore(fechaVencimiento);
    }

    public boolean estaSinUsar() {
        return puntosDisponibles == puntosIniciales;
    }

    public int getPuntosIniciales() {
        return puntosIniciales;
    }

    public int getPuntosDisponibles() {
        return puntosDisponibles;
    }

    public void setPuntosDisponibles(int puntosDisponibles) {
        if (puntosDisponibles < 0 || puntosDisponibles > puntosIniciales) {
            throw new IllegalArgumentException("Los puntos disponibles deben estar entre 0 y " + puntosIniciales);
        }
        this.puntosDisponibles = puntosDisponibles;
    }

    public LocalDate getFechaObtencion() {
        return fechaObtencion;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public Compra getCompraOrigen() {
        return compraOrigen;
    }

    public boolean isAnulada() {
        return anulada;
    }

    public void setAnulada(boolean anulada) {
        this.anulada = anulada;
    }

    @Override
    public String toString() {
        return "AcumulacionPuntos{iniciales=" + puntosIniciales + ", disponibles=" + puntosDisponibles
                + ", obtencion=" + fechaObtencion + ", vencimiento=" + fechaVencimiento
                + ", anulada=" + anulada + "}";
    }
}
