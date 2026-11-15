package org.example.proyectofinalprogramacion2.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * Pestaña Notificaciones: avisos que recibe el cliente (por ejemplo, funciones canceladas por el cine).
 */
public class NotificacionesController implements PestanaCliente {

    @FXML
    private ListView<String> listaNotificaciones;

    private ClienteController principal;

    @Override
    public void inicializar(ClienteController principal) {
        this.principal = principal;
    }

    @Override
    public void refrescar() {
        listaNotificaciones.setItems(FXCollections.observableArrayList(principal.getCliente().getNotificaciones()));
    }
}
