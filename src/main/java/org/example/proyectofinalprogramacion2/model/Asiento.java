package org.example.proyectofinalprogramacion2.model;

import java.util.Objects;

/**
 * Asiento físico de una sala. Su disponibilidad se controla en cada Funcion.
 */
public class Asiento {

    private String fila;
    private int numero;
    private TipoAsiento tipo;

    public Asiento(String fila, int numero, TipoAsiento tipo) {
        setFila(fila);
        setNumero(numero);
        setTipo(tipo);
    }

    /**
     * Código legible del asiento, por ejemplo "B7".
     */
    public String getCodigo() {
        return fila + numero;
    }

    public boolean esAccesible() {
        return tipo == TipoAsiento.ACCESIBLE;
    }

    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = Validador.texto(fila, "fila").toUpperCase();
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = Validador.positivo(numero, "número de asiento");
    }

    public TipoAsiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoAsiento tipo) {
        this.tipo = Validador.noNulo(tipo, "tipo de asiento");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Asiento)) return false;
        Asiento asiento = (Asiento) o;
        return numero == asiento.numero && fila.equals(asiento.fila);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, numero);
    }

    @Override
    public String toString() {
        return getCodigo() + (esAccesible() ? " (accesible)" : "");
    }
}
