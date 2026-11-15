package org.example.proyectofinalprogramacion2.controller;

/**
 * Controlador de una pestaña de la ventana del administrador.
 * La ventana principal le entrega su referencia y le pide refrescarse cada vez que se abre la pestaña.
 */
public interface PestanaAdmin {

    void inicializar(AdminController principal);

    void refrescar();
}
