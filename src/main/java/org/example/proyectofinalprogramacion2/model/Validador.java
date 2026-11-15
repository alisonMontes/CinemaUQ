package org.example.proyectofinalprogramacion2.model;

/**
 * Validaciones simples reutilizadas por las clases del modelo.
 */
final class Validador {

    private Validador() {
    }

    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        return valor.trim();
    }

    static <T> T noNulo(T valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio");
        }
        return valor;
    }

    static double positivo(double valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser mayor que cero");
        }
        return valor;
    }

    static double noNegativo(double valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser negativo");
        }
        return valor;
    }

    static int positivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException("El campo " + campo + " debe ser mayor que cero");
        }
        return valor;
    }
}
