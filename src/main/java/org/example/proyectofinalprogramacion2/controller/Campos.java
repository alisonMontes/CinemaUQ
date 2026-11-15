package org.example.proyectofinalprogramacion2.controller;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextInputControl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Lectura de los campos de los formularios. Si un dato no se puede leer lanza
 * IllegalArgumentException con un mensaje claro para mostrarlo al usuario.
 */
public final class Campos {

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("H:mm");

    private Campos() {
    }

    public static String texto(TextInputControl campo) {
        return campo.getText() == null ? "" : campo.getText().trim();
    }

    /**
     * Valor en pesos; acepta "20000", "20.000" o "$20.000".
     */
    public static double dinero(TextInputControl campo, String nombre) {
        String valor = texto(campo).replace("$", "").replace(".", "").replace(" ", "").replace(',', '.');
        return numero(valor, nombre);
    }

    /**
     * Número con decimales opcionales; acepta "12.5" o "12,5".
     */
    public static double decimal(TextInputControl campo, String nombre) {
        return numero(texto(campo).replace(',', '.'), nombre);
    }

    public static int entero(TextInputControl campo, String nombre) {
        try {
            return Integer.parseInt(texto(campo));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + nombre + " debe ser un número entero");
        }
    }

    /**
     * Hora con formato HH:mm, por ejemplo 19:30.
     */
    public static LocalTime hora(TextInputControl campo) {
        try {
            return LocalTime.parse(texto(campo), HORA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("La hora debe tener el formato HH:mm (por ejemplo 19:30)");
        }
    }

    public static LocalDate fecha(DatePicker campo, String nombre) {
        if (campo.getValue() == null) {
            throw new IllegalArgumentException("Elige la " + nombre);
        }
        return campo.getValue();
    }

    public static <T> T seleccion(ComboBox<T> campo, String nombre) {
        if (campo.getValue() == null) {
            throw new IllegalArgumentException("Elige " + nombre);
        }
        return campo.getValue();
    }

    private static double numero(String valor, String nombre) {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + nombre + " debe ser un número");
        }
    }
}
