package org.example.proyectofinalprogramacion2.report;

/**
 * Reportes que se pueden exportar. La clave se usa en el nombre del archivo.
 */
public enum TipoReporte {

    VENTAS("ventas", "Ventas por periodo"),
    ENTRADAS_POR_PELICULA("entradas_pelicula", "Entradas vendidas por película"),
    ENTRADAS_POR_FUNCION("entradas_funcion", "Entradas vendidas por función"),
    OCUPACION_SALAS("ocupacion_salas", "Ocupación de salas"),
    PRODUCTOS_COMBOS("productos_combos", "Productos y combos vendidos"),
    RECARGAS("recargas", "Recargas realizadas"),
    REEMBOLSOS("reembolsos", "Cancelaciones y reembolsos"),
    PUNTOS("puntos", "Puntos acumulados y redimidos");

    private final String clave;
    private final String descripcion;

    TipoReporte(String clave, String descripcion) {
        this.clave = clave;
        this.descripcion = descripcion;
    }

    public String getClave() {
        return clave;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
