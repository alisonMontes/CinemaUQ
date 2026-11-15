package org.example.proyectofinalprogramacion2.factory;

import org.example.proyectofinalprogramacion2.exception.TipoAsientoInvalidoException;
import org.example.proyectofinalprogramacion2.model.Asiento;
import org.example.proyectofinalprogramacion2.model.Entrada;
import org.example.proyectofinalprogramacion2.model.EntradaGeneral;
import org.example.proyectofinalprogramacion2.model.Funcion;

/**
 * Crea entradas generales (exige asiento NORMAL).
 */
// SRP: solo crea entradas generales.
public class EntradaGeneralFactory extends EntradaFactory {

    @Override
    public Entrada crearEntrada(Funcion funcion, Asiento asiento) throws TipoAsientoInvalidoException {
        return new EntradaGeneral(funcion, asiento);
    }
}
