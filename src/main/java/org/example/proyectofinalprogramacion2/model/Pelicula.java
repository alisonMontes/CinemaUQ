package org.example.proyectofinalprogramacion2.model;

import java.util.Objects;

/**
 * Película que puede programarse en funciones.
 */
public class Pelicula {

    private String codigo;
    private String titulo;
    private String genero;
    private int duracionMinutos;
    private String clasificacion;
    private String sinopsis;
    private boolean enCartelera;

    public Pelicula(String codigo, String titulo, String genero, int duracionMinutos,
                    String clasificacion, String sinopsis) {
        setCodigo(codigo);
        setTitulo(titulo);
        setGenero(genero);
        setDuracionMinutos(duracionMinutos);
        setClasificacion(clasificacion);
        setSinopsis(sinopsis);
        this.enCartelera = true;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = Validador.texto(codigo, "código");
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = Validador.texto(titulo, "título");
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = Validador.texto(genero, "género");
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = Validador.positivo(duracionMinutos, "duración");
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = Validador.texto(clasificacion, "clasificación");
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis == null ? "" : sinopsis.trim();
    }

    public boolean isEnCartelera() {
        return enCartelera;
    }

    public void setEnCartelera(boolean enCartelera) {
        this.enCartelera = enCartelera;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pelicula)) return false;
        return codigo.equals(((Pelicula) o).codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return titulo + " (" + clasificacion + ", " + duracionMinutos + " min)";
    }
}
