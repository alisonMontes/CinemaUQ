package org.example.proyectofinalprogramacion2.observer;

import org.example.proyectofinalprogramacion2.model.Funcion;

/**
 * Observer: quien quiera enterarse de los cambios de una función (por ejemplo, su cancelación).
 */
// ISP: interfaz de un solo método; quien observa no depende de nada más de la función.
public interface ObservadorFuncion {

    /** Recibe el aviso de la función (por ejemplo, que fue cancelada). */
    void actualizar(Funcion funcion, String mensaje);
}
