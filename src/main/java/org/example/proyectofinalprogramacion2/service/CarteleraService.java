package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.CinemaUQ;
import org.example.proyectofinalprogramacion2.model.Combo;
import org.example.proyectofinalprogramacion2.model.Funcion;
import org.example.proyectofinalprogramacion2.model.Pelicula;
import org.example.proyectofinalprogramacion2.model.Producto;
import org.example.proyectofinalprogramacion2.model.Promocion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Consultas de cartelera y catálogo (confitería, combos y promociones vigentes) para el cliente.
 */
// SRP: solo consulta películas, funciones, asientos y catálogo; no vende.
public class CarteleraService {

    private final CinemaUQ cinema;

    public CarteleraService() {
        this(CinemaUQ.getInstancia());
    }

    public CarteleraService(CinemaUQ cinema) {
        this.cinema = cinema;
    }

    public List<Pelicula> getPeliculasEnCartelera() {
        List<Pelicula> enCartelera = new ArrayList<>();
        for (Pelicula pelicula : cinema.getPeliculas()) {
            if (pelicula.isEnCartelera()) {
                enCartelera.add(pelicula);
            }
        }
        return enCartelera;
    }

    /**
     * Funciones PROGRAMADAS de la película que aún no han empezado, ordenadas por fecha y hora.
     */
    public List<Funcion> getFuncionesDisponibles(Pelicula pelicula, LocalDateTime ahora) {
        List<Funcion> disponibles = new ArrayList<>();
        for (Funcion funcion : cinema.getFunciones()) {
            if (funcion.getPelicula().equals(pelicula) && funcion.estaDisponibleParaVenta(ahora)) {
                disponibles.add(funcion);
            }
        }
        disponibles.sort(Comparator.comparing(Funcion::getFechaHora));
        return disponibles;
    }

    public List<Asiento> getAsientosDisponibles(Funcion funcion) {
        return funcion.getAsientosDisponibles();
    }

    public List<Producto> getProductos() {
        return cinema.getProductos();
    }

    public List<Combo> getCombos() {
        return cinema.getCombos();
    }

    /**
     * Promociones que el cliente puede aplicar hoy.
     */
    public List<Promocion> getPromocionesVigentes(LocalDate hoy) {
        List<Promocion> vigentes = new ArrayList<>();
        for (Promocion promocion : cinema.getPromociones()) {
            if (promocion.estaVigente(hoy)) {
                vigentes.add(promocion);
            }
        }
        return vigentes;
    }
}
