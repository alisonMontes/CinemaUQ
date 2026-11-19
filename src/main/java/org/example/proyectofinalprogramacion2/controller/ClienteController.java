package org.example.proyectofinalprogramacion2.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.example.proyectofinalprogramacion2.model.Cliente;
import org.example.proyectofinalprogramacion2.model.Usuario;

import java.util.HashMap;
import java.util.Map;

/**
 * Ventana principal del cliente. Contiene las pestañas (cada una con su controlador),
 * guarda el carrito en curso y permite pasar de una pestaña a otra.
 */
public class ClienteController {

    @FXML
    private Label lblBienvenida;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabCartelera;
    @FXML
    private Tab tabAsientos;
    @FXML
    private Tab tabConfiteria;
    @FXML
    private Tab tabResumen;
    @FXML
    private Tab tabTarjeta;
    @FXML
    private Tab tabCompras;
    @FXML
    private Tab tabPuntos;
    @FXML
    private Tab tabNotificaciones;

    // Controladores de las pestañas incluidas con fx:include (fx:id + "Controller").
    @FXML
    private CarteleraController carteleraController;
    @FXML
    private AsientosController asientosController;
    @FXML
    private ConfiteriaController confiteriaController;
    @FXML
    private ResumenPagoController resumenController;
    @FXML
    private TarjetaController tarjetaController;
    @FXML
    private ComprasController comprasController;
    @FXML
    private PuntosController puntosController;
    @FXML
    private NotificacionesController notificacionesController;

    private final Map<Tab, PestanaCliente> pestanas = new HashMap<>();
    private final CarritoCompra carrito = new CarritoCompra();
    private Cliente cliente;

    /**
     * Recibe el usuario autenticado y prepara todas las pestañas.
     */
    public void initData(Usuario usuario) {
        this.cliente = (Cliente) usuario;
        lblBienvenida.setText("Hola, " + cliente.getNombre());

        pestanas.put(tabCartelera, carteleraController);
        pestanas.put(tabAsientos, asientosController);
        pestanas.put(tabConfiteria, confiteriaController);
        pestanas.put(tabResumen, resumenController);
        pestanas.put(tabTarjeta, tarjetaController);
        pestanas.put(tabCompras, comprasController);
        pestanas.put(tabPuntos, puntosController);
        pestanas.put(tabNotificaciones, notificacionesController);
        for (PestanaCliente pestana : pestanas.values()) {
            pestana.inicializar(this);
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, anterior, nueva) -> refrescar(nueva));
        refrescar(tabPane.getSelectionModel().getSelectedItem());
    }

    private void refrescar(Tab tab) {
        PestanaCliente pestana = pestanas.get(tab);
        if (pestana != null) {
            pestana.refrescar();
        }
    }

    public Cliente getCliente() {
        return cliente;
    }

    public CarritoCompra getCarrito() {
        return carrito;
    }

    public void irAAsientos() {
        tabPane.getSelectionModel().select(tabAsientos);
    }

    public void irAConfiteria() {
        tabPane.getSelectionModel().select(tabConfiteria);
    }

    public void irAResumen() {
        tabPane.getSelectionModel().select(tabResumen);
    }

    public void irACartelera() {
        tabPane.getSelectionModel().select(tabCartelera);
    }

    /** Botón para cerrar sesión y volver al inicio de sesión. */
    @FXML
    private void cerrarSesion() {
        NavegadorVistas.cambiarVista(NavegadorVistas.ventanaDe(tabPane), NavegadorVistas.LOGIN, "Iniciar sesión");
    }
}
