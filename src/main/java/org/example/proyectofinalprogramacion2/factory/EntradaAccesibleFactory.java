package org.example.proyectofinalprogramacion2.factory;

import org.example.proyectofinalprogramacion2.exception.TipoAsientoInvalidoException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Entrada;
import org.example.proyectofinalprogramacion2.model.EntradaAccesible;
import org.example.proyectofinalprogramacion2.model.Funcion;

/**
 * Crea entradas accesibles (exige asiento ACCESIBLE).
 */
// SRP: solo crea entradas accesibles.
public class EntradaAccesibleFactory extends EntradaFactory {

    @Override
    public Entrada crearEntrada(Funcion funcion, Asiento asiento) throws TipoAsientoInvalidoException {
        return new EntradaAccesible(funcion, asiento);
    }
}
