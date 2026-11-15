package org.example.proyectofinalprogramacion2.factory;

import org.example.proyectofinalprogramacion2.exception.TipoAsientoInvalidoException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Entrada;
import org.example.proyectofinalprogramacion2.model.Funcion;

/**
 * Factory Method: cada fábrica concreta decide qué subclase de Entrada crear.
 * La validación del tipo de asiento está en el constructor de cada subclase de Entrada.
 */
// OCP: un nuevo tipo de entrada se agrega con su subclase y su fábrica, sin modificar las existentes.
public abstract class EntradaFactory {

    public abstract Entrada crearEntrada(Funcion funcion, Asiento asiento) throws TipoAsientoInvalidoException;

    /**
     * Fábrica que corresponde al tipo del asiento elegido.
     */
    public static EntradaFactory segunAsiento(Asiento asiento) {
        return asiento.esAccesible() ? new EntradaAccesibleFactory() : new EntradaGeneralFactory();
    }
}
