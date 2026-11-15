package org.example.proyectofinalprogramacion2;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.proyectofinalprogramacion2.controller.NavegadorVistas;
import org.example.proyectofinalprogramacion2.service.DatosIniciales;

import java.time.LocalDateTime;

public class HelloApplication extends Application {

    @Override
    public void init() {
        DatosIniciales.cargar(LocalDateTime.now());
    }

    @Override
    public void start(Stage stage) {
        NavegadorVistas.cambiarVista(stage, NavegadorVistas.LOGIN, "Iniciar sesión");
    }
}
