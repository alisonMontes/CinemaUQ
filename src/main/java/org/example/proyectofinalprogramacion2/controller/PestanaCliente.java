package org.example.proyectofinalprogramacion2.controller;

/**
 * Controlador de una pestaña de la ventana del cliente.
 * La ventana principal le entrega su referencia y le pide refrescarse cada vez que se abre la pestaña.
 */
public interface PestanaCliente {

    void inicializar(ClienteController principal);

    void refrescar();
}
