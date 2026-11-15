package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.AsientoNoDisponibleException;
import org.example.proyectofinalprogramacion2.observer.ObservadorFuncion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Proyección de una película en una sala, en una fecha y hora.
 * Guarda los asientos ocupados de esta función y es el sujeto observado (Observer):
 * avisa a sus observadores cuando se cancela.
 */
public class Funcion {

    private String codigo;
    private Pelicula pelicula;
    private Sala sala;
    private LocalDate fecha;
    private LocalTime hora;
    private double precioBase;
    private EstadoFuncion estado;
    private final List<Asiento> asientosOcupados;
    private final List<ObservadorFuncion> observadores;

    public Funcion(String codigo, Pelicula pelicula, Sala sala, LocalDate fecha, LocalTime hora, double precioBase) {
        setCodigo(codigo);
        setPelicula(pelicula);
        setSala(sala);
        setFecha(fecha);
        setHora(hora);
        setPrecioBase(precioBase);
        this.estado = EstadoFuncion.PROGRAMADA;
        this.asientosOcupados = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    // ---------- Observer ----------

    public void agregarObservador(ObservadorFuncion observador) {
        Validador.noNulo(observador, "observador");
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public boolean eliminarObservador(ObservadorFuncion observador) {
        return observadores.remove(observador);
    }

    public void notificarObservadores(String mensaje) {
        for (ObservadorFuncion observador : new ArrayList<>(observadores)) {
            observador.actualizar(this, mensaje);
        }
    }

    /**
     * El cine cancela la función y avisa a sus observadores.
     */
    public void cancelar() {
        if (estado == EstadoFuncion.CANCELADA) {
            throw new IllegalStateException("La función " + codigo + " ya está cancelada");
        }
        this.estado = EstadoFuncion.CANCELADA;
        notificarObservadores("La función de " + pelicula.getTitulo() + " del " + fecha + " a las " + hora
                + " en " + sala.getNombre() + " fue cancelada por el cine. Las compras pagadas con tarjeta"
                + " se reembolsan al 100 %.");
    }

    /**
     * Una función se puede vender o canjear si está PROGRAMADA y todavía no ha empezado.
     */
    public boolean estaDisponibleParaVenta(LocalDateTime ahora) {
        return estado == EstadoFuncion.PROGRAMADA && ahora.isBefore(getFechaHora());
    }

    public List<ObservadorFuncion> getObservadores() {
        return Collections.unmodifiableList(observadores);
    }

    // ---------- Asientos ----------

    public LocalDateTime getFechaHora() {
        return LocalDateTime.of(fecha, hora);
    }

    public boolean estaDisponible(Asiento asiento) {
        return sala.contieneAsiento(asiento) && !asientosOcupados.contains(asiento);
    }

    public void ocuparAsiento(Asiento asiento) throws AsientoNoDisponibleException {
        Validador.noNulo(asiento, "asiento");
        if (!sala.contieneAsiento(asiento)) {
            throw new IllegalArgumentException("El asiento " + asiento.getCodigo() + " no pertenece a la sala");
        }
        if (asientosOcupados.contains(asiento)) {
            throw new AsientoNoDisponibleException("El asiento " + asiento.getCodigo()
                    + " ya está ocupado en la función " + codigo);
        }
        asientosOcupados.add(asiento);
    }

    public boolean liberarAsiento(Asiento asiento) {
        return asientosOcupados.remove(asiento);
    }

    public List<Asiento> getAsientosDisponibles() {
        List<Asiento> disponibles = new ArrayList<>();
        for (Asiento asiento : sala.getAsientos()) {
            if (!asientosOcupados.contains(asiento)) {
                disponibles.add(asiento);
            }
        }
        return disponibles;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = Validador.texto(codigo, "código");
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = Validador.noNulo(pelicula, "película");
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = Validador.noNulo(sala, "sala");
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = Validador.noNulo(fecha, "fecha");
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = Validador.noNulo(hora, "hora");
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = Validador.positivo(precioBase, "precio base");
    }

    public EstadoFuncion getEstado() {
        return estado;
    }

    public void setEstado(EstadoFuncion estado) {
        this.estado = Validador.noNulo(estado, "estado");
    }

    public List<Asiento> getAsientosOcupados() {
        return Collections.unmodifiableList(asientosOcupados);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Funcion)) return false;
        return codigo.equals(((Funcion) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return pelicula.getTitulo() + " - " + sala.getNombre() + " - " + fecha + " " + hora
                + " (" + estado + ")";
    }
}
