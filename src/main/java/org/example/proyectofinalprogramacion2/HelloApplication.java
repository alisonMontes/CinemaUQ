package org.example.proyectofinalprogramacion2;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.proyectofinalprogramacion2.controller.NavegadorVistas;
import org.example.proyectofinalprogramacion2.service.DatosIniciales;

import java.time.LocalDateTime;

/**
 * Aplicación JavaFX: carga los datos de demostración y abre la pantalla de inicio de sesión.
 */
public class HelloApplication extends Application {

    /** Carga los datos iniciales antes de mostrar la ventana. */
    @Override
    public void init() {
        DatosIniciales.cargar(LocalDateTime.now());
    }

    /** Muestra la vista de inicio de sesión. */
    @Override
    public void start(Stage stage) {
        NavegadorVistas.cambiarVista(stage, NavegadorVistas.LOGIN, "Iniciar sesión");
    }
}
