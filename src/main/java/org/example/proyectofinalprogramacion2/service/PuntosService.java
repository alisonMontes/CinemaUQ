package org.example.proyectofinalprogramacion2.service;

import org.example.proyectofinalprogramacion2.exception.PuntosInsuficientesException;
import org.example.proyectofinalprogramacion2.model.AcumulacionPuntos;
import org.example.proyectofinalprogramacion2.model.Cliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Consulta y consumo de los puntos de un cliente.
 */
// SRP: solo consulta y descuenta puntos; qué beneficio se entrega lo decide CompraService.
public class PuntosService {

    public int getPuntosVigentes(Cliente cliente, LocalDate hoy) {
        return cliente.getCuentaPuntos().getPuntosDisponibles(hoy);
    }

    public List<AcumulacionPuntos> getAcumulacionesVigentes(Cliente cliente, LocalDate hoy) {
        return cliente.getCuentaPuntos().getAcumulacionesVigentes(hoy);
    }

    /**
     * Acumulaciones vencidas que todavía tenían puntos sin usar (no anuladas).
     */
    public List<AcumulacionPuntos> getAcumulacionesVencidas(Cliente cliente, LocalDate hoy) {
        List<AcumulacionPuntos> vencidas = new ArrayList<>();
        for (AcumulacionPuntos acumulacion : cliente.getCuentaPuntos().getAcumulaciones()) {
            if (!acumulacion.isAnulada() && acumulacion.estaVencida(hoy) && acumulacion.getPuntosDisponibles() > 0) {
                vencidas.add(acumulacion);
            }
        }
        return vencidas;
    }

    public int getPuntosVencidos(Cliente cliente, LocalDate hoy) {
        int total = 0;
        for (AcumulacionPuntos acumulacion : getAcumulacionesVencidas(cliente, hoy)) {
            total += acumulacion.getPuntosDisponibles();
        }
        return total;
    }

    /**
     * Descuenta puntos usando primero las acumulaciones más próximas a vencer.
     * Los vencidos y anulados nunca se usan. Si no alcanzan, no se descuenta nada.
     */
    public void redimir(Cliente cliente, int puntos, LocalDate hoy) throws PuntosInsuficientesException {
        if (puntos <= 0) {
            throw new IllegalArgumentException("Los puntos a redimir deben ser mayores que cero");
        }
        int disponibles = getPuntosVigentes(cliente, hoy);
        if (disponibles < puntos) {
            throw new PuntosInsuficientesException("Puntos insuficientes: tienes " + disponibles
                    + " vigentes y se requieren " + puntos);
        }
        int pendientes = puntos;
        for (AcumulacionPuntos acumulacion : getAcumulacionesVigentes(cliente, hoy)) {
            int usados = Math.min(pendientes, acumulacion.getPuntosDisponibles());
            acumulacion.setPuntosDisponibles(acumulacion.getPuntosDisponibles() - usados);
            pendientes -= usados;
            if (pendientes == 0) {
                break;
            }
        }
    }
}
