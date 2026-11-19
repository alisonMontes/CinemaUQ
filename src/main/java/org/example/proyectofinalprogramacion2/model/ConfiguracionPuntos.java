package org.example.proyectofinalprogramacion2.model;

/**
 * Constantes del programa de puntos: regla de acumulación y equivalencias de redención.
 */
public final class ConfiguracionPuntos {

    /** Se obtiene 1 punto por cada PESOS_POR_PUNTO pagados con tarjeta. */
    public static final double PESOS_POR_PUNTO = 100;

    /** Años de vigencia de cada acumulación de puntos. */
    public static final int ANIOS_VIGENCIA = 1;

    /** Puntos necesarios para redimir una entrada. */
    public static final int PUNTOS_ENTRADA = 1000;

    /** Puntos necesarios para redimir un producto. */
    public static final int PUNTOS_PRODUCTO = 400;

    /** Puntos necesarios para redimir un combo. */
    public static final int PUNTOS_COMBO = 1200;

    private ConfiguracionPuntos() {
    }

    /**
     * Calcula los puntos que genera un pago con tarjeta (se descartan las fracciones).
     * Ejemplo: un pago de $25.050 genera 250 puntos.
     */
    public static int calcularPuntos(double montoPagado) {
        if (montoPagado <= 0) {
            return 0;
        }
        return (int) (montoPagado / PESOS_POR_PUNTO);
    }
}
