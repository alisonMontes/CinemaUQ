package org.example.proyectofinalprogramacion2.model;

import org.example.proyectofinalprogramacion2.exception.TipoAsientoInvalidoException;

/**
 * Entrada para un asiento NORMAL.
 */
// LSP: se usa donde se espere una Entrada o un ItemVendible sin cambiar el comportamiento esperado.
public class EntradaGeneral extends Entrada {

    public EntradaGeneral(Funcion funcion, Asiento asiento) throws TipoAsientoInvalidoException {
        super(funcion, asiento);
        if (asiento.getTipo() != TipoAsiento.NORMAL) {
            throw new TipoAsientoInvalidoException("Una entrada general requiere un asiento normal ("
                    + asiento.getCodigo() + " es accesible)");
        }
    }

    @Override
    public String getTipo() {
        return "general";
    }
}
