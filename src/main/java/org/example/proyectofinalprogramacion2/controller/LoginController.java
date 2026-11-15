package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Usuario;
import org.example.proyectofinalprogramacion2.service.AutenticacionService;

/**
 * Pantalla de inicio de sesión.
 */
public class LoginController {

    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtClave;
    @FXML
    private Label lblMensaje;

    private final AutenticacionService autenticacionService = new AutenticacionService();

    @FXML
    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();
        if (correo.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Ingresa tu correo y tu clave");
            return;
        }
        try {
            Usuario usuario = autenticacionService.iniciarSesion(correo, clave);
            if (usuario instanceof Cliente) {
                ClienteController controller = NavegadorVistas.cambiarVista(
                        NavegadorVistas.ventanaDe(txtCorreo), NavegadorVistas.CLIENTE, "Cliente");
                controller.initData(usuario);
            } else {
                AdminController controller = NavegadorVistas.cambiarVista(
                        NavegadorVistas.ventanaDe(txtCorreo), AdminController.VISTA, "Administrador");
                controller.initData(usuario);
            }
        } catch (CinemaUQException e) {
            lblMensaje.setText(e.getMessage());
            txtClave.clear();
        }
    }

    @FXML
    private void irARegistro() {
        NavegadorVistas.cambiarVista(NavegadorVistas.ventanaDe(txtCorreo), NavegadorVistas.REGISTRO, "Registro");
    }
}
