package org.example.proyectofinalprogramacion2.controller;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formato de dinero y fechas para mostrar en la interfaz.
 */
public final class Formato {

    private static final DecimalFormat DINERO;
    private static final DecimalFormat PORCENTAJE;
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        DINERO = new DecimalFormat("$#,##0", simbolos);
        PORCENTAJE = new DecimalFormat("#,##0.##", simbolos);
    }

    private Formato() {
    }

    public static String dinero(double valor) {
        return DINERO.format(valor);
    }

    /**
     * Por ejemplo 20 -> "20 %" y 12.5 -> "12,5 %".
     */
    public static String porcentaje(double valor) {
        return PORCENTAJE.format(valor) + " %";
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "" : FECHA.format(fecha);
    }

    public static String fechaHora(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : FECHA_HORA.format(fechaHora);
    }
}
