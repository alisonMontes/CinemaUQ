module org.example.proyectofinalprogramacion2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.example.proyectofinalprogramacion2 to javafx.fxml;
    exports org.example.proyectofinalprogramacion2;
    exports org.example.proyectofinalprogramacion2.model;
    exports org.example.proyectofinalprogramacion2.exception;
    exports org.example.proyectofinalprogramacion2.factory;
    exports org.example.proyectofinalprogramacion2.decorator;
    exports org.example.proyectofinalprogramacion2.facade;
    exports org.example.proyectofinalprogramacion2.adapter;
    exports org.example.proyectofinalprogramacion2.strategy;
    exports org.example.proyectofinalprogramacion2.observer;
    exports org.example.proyectofinalprogramacion2.service;
}
