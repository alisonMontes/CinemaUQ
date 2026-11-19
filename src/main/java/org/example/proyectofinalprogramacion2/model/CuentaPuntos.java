package org.example.proyectofinalprogramacion2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Cuenta de puntos de un cliente, formada por sus acumulaciones.
 */
public class CuentaPuntos {

    private final List<AcumulacionPuntos> acumulaciones;

    public CuentaPuntos() {
        this.acumulaciones = new ArrayList<>();
    }

    public void agregarAcumulacion(AcumulacionPuntos acumulacion) {
        acumulaciones.add(Validador.noNulo(acumulacion, "acumulación"));
    }

    /**
     * Acumulaciones que se pueden usar hoy, ordenadas de la más próxima a vencer a la más lejana.
     * Este orden es el que se usa al redimir.
     */
    public List<AcumulacionPuntos> getAcumulacionesVigentes(LocalDate hoy) {
        List<AcumulacionPuntos> vigentes = new ArrayList<>();
        for (AcumulacionPuntos acumulacion : acumulaciones) {
            if (!acumulacion.isAnulada() && !acumulacion.estaVencida(hoy) && acumulacion.getPuntosDisponibles() > 0) {
                vigentes.add(acumulacion);
            }
        }
        vigentes.sort(Comparator.comparing(AcumulacionPuntos::getFechaVencimiento));
        return vigentes;
    }

    /**
     * Anula los puntos generados por una compra si siguen sin usar.
     *
     * @return true si se anuló una acumulación; false si no había o ya se habían usado
     */
    public boolean anularPuntosDe(Compra compra) {
        for (AcumulacionPuntos acumulacion : acumulaciones) {
            if (compra.equals(acumulacion.getCompraOrigen()) && !acumulacion.isAnulada()
                    && acumulacion.estaSinUsar()) {
                acumulacion.setAnulada(true);
                return true;
            }
        }
        return false;
    }

    /** Total de puntos vigentes en la fecha indicada. */
    public int getPuntosDisponibles(LocalDate hoy) {
        int total = 0;
        for (AcumulacionPuntos acumulacion : getAcumulacionesVigentes(hoy)) {
            total += acumulacion.getPuntosDisponibles();
        }
        return total;
    }

    public List<AcumulacionPuntos> getAcumulaciones() {
        return Collections.unmodifiableList(acumulaciones);
    }

    @Override
    public String toString() {
        return "CuentaPuntos{acumulaciones=" + acumulaciones.size() + "}";
    }
}
