package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.proyectofinalprogramacion2.exception.CinemaUQException;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.service.AutenticacionService;

/**
 * Pantalla de registro de un cliente nuevo.
 */
public class RegistroController {

    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtCorreo;
    @FXML
    private PasswordField txtClave;
    @FXML
    private PasswordField txtConfirmacion;
    @FXML
    private Label lblMensaje;

    private final AutenticacionService autenticacionService = new AutenticacionService();

    @FXML
    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String clave = txtClave.getText();
        if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            lblMensaje.setText("Completa todos los campos");
            return;
        }
        if (!clave.equals(txtConfirmacion.getText())) {
            lblMensaje.setText("Las claves no coinciden");
            return;
        }
        try {
            Cliente cliente = autenticacionService.registrarCliente(nombre, correo, clave);
            Dialogos.info("Registro exitoso", "Bienvenido(a), " + cliente.getNombre()
                    + ". Tu tarjeta virtual " + cliente.getTarjeta().getNumero()
                    + " quedó activa con saldo " + Formato.dinero(cliente.getTarjeta().getSaldo())
                    + ". Solicita una recarga al administrador para comprar.");
            ClienteController controller = NavegadorVistas.cambiarVista(
                    NavegadorVistas.ventanaDe(txtNombre), NavegadorVistas.CLIENTE, "Cliente");
            controller.initData(cliente);
        } catch (CinemaUQException e) {
            lblMensaje.setText(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Validaciones de datos del modelo (por ejemplo, un correo sin @).
            lblMensaje.setText(e.getMessage());
        }
    }

    @FXML
    private void volver() {
        NavegadorVistas.cambiarVista(NavegadorVistas.ventanaDe(txtNombre), NavegadorVistas.LOGIN, "Iniciar sesión");
    }
}
