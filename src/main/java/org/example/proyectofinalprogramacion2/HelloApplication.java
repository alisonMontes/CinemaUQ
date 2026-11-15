package org.example.proyectofinalprogramacion2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.proyectofinalprogramacion2.service.DatosIniciales;

import java.io.IOException;
import java.time.LocalDateTime;

public class HelloApplication extends Application {
    @Override
    public void init() {
        DatosIniciales.cargar(LocalDateTime.now());
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
