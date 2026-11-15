package org.example.proyectofinalprogramacion2.report;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Formato de dinero, porcentajes y fechas en los reportes TXT (sin depender de la interfaz).
 */
final class FormatoReporte {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private FormatoReporte() {
    }

    static String dinero(double valor) {
        return new DecimalFormat("$#,##0", simbolos()).format(valor);
    }

    static String porcentaje(double valor) {
        return new DecimalFormat("0.00", simbolos()).format(valor) + " %";
    }

    static String fecha(LocalDate fecha) {
        return FECHA.format(fecha);
    }

    static String fechaHora(LocalDateTime fechaHora) {
        return FECHA_HORA.format(fechaHora);
    }

    private static DecimalFormatSymbols simbolos() {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');
        return simbolos;
    }
}
