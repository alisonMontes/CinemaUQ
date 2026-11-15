package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Carga un archivo FXML de la carpeta view, le aplica la hoja de estilos y lo pone como escena
 * de la ventana. Devuelve el controlador para que quien navega le pase los datos (initData).
 */
public final class NavegadorVistas {

    public static final String LOGIN = "login-view.fxml";
    public static final String REGISTRO = "registro-view.fxml";
    public static final String CLIENTE = "cliente-view.fxml";

    private static final String RUTA_VISTAS = "/org/example/proyectofinalprogramacion2/view/";
    private static final String HOJA_ESTILOS = "styles.css";

    private NavegadorVistas() {
    }

    /**
     * Cambia la escena de la ventana por la vista indicada.
     *
     * @return el controlador de la nueva vista
     */
    public static <T> T cambiarVista(Stage stage, String archivoFxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(recurso(archivoFxml));
            Parent raiz = loader.load();
            Scene escena = new Scene(raiz);
            escena.getStylesheets().add(recurso(HOJA_ESTILOS).toExternalForm());
            stage.setTitle("CinemaUQ - " + titulo);
            stage.setScene(escena);
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
            return loader.getController();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar la vista " + archivoFxml, e);
        }
    }

    /**
     * Ventana a la que pertenece un nodo de la vista actual.
     */
    public static Stage ventanaDe(Node nodo) {
        return (Stage) nodo.getScene().getWindow();
    }

    private static URL recurso(String nombre) {
        URL url = NavegadorVistas.class.getResource(RUTA_VISTAS + nombre);
        if (url == null) {
            throw new IllegalStateException("No se encontró el recurso " + nombre);
        }
        return url;
    }
}
