package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.TipoAsientoInvalidoException;

/**
 * Entrada para un asiento ACCESIBLE.
 */
// LSP: se usa donde se espere una Entrada o un ItemVendible sin cambiar el comportamiento esperado.
public class EntradaAccesible extends Entrada {

    public EntradaAccesible(Funcion funcion, Asiento asiento) throws TipoAsientoInvalidoException {
        super(funcion, asiento);
        if (asiento.getTipo() != TipoAsiento.ACCESIBLE) {
            throw new TipoAsientoInvalidoException("Una entrada accesible requiere un asiento accesible ("
                    + asiento.getCodigo() + " es normal)");
        }
    }

    @Override
    public String getTipo() {
        return "accesible";
    }
}
