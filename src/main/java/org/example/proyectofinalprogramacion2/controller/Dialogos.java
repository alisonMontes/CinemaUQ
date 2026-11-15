package org.example.proyectofinalprogramacion2.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Ventanas emergentes comunes de la interfaz (errores, avisos y confirmaciones).
 */
public final class Dialogos {

    private Dialogos() {
    }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "No se pudo completar la operación", mensaje);
    }

    public static void info(String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    /**
     * @return true si el usuario acepta
     */
    public static boolean confirmar(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, mensaje, ButtonType.OK, ButtonType.CANCEL);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        return alerta.showAndWait().filter(boton -> boton == ButtonType.OK).isPresent();
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
