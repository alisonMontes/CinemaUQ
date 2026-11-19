package org.example.proyectofinalprogramacion2.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tabla de texto de un reporte: encabezados y filas alineados en columnas.
 */
// SRP: solo arma y alinea las columnas de texto.
public class TablaReporte {

    private final String[] encabezados;
    private final boolean[] alineadaDerecha;
    private final List<String[]> filas = new ArrayList<>();

    public TablaReporte(String... encabezados) {
        if (encabezados.length == 0) {
            throw new IllegalArgumentException("La tabla necesita al menos una columna");
        }
        this.encabezados = encabezados.clone();
        this.alineadaDerecha = new boolean[encabezados.length];
    }

    /**
     * Marca columnas numéricas para alinearlas a la derecha (índices desde 0).
     */
    public TablaReporte alinearDerecha(int... columnas) {
        for (int columna : columnas) {
            alineadaDerecha[columna] = true;
        }
        return this;
    }

    /** Agrega una fila; debe tener un valor por columna. */
    public void agregarFila(String... valores) {
        if (valores.length != encabezados.length) {
            throw new IllegalArgumentException("La fila debe tener " + encabezados.length + " valores");
        }
        filas.add(valores.clone());
    }

    public boolean estaVacia() {
        return filas.isEmpty();
    }

    public int getCantidadFilas() {
        return filas.size();
    }

    public List<String[]> getFilas() {
        return Collections.unmodifiableList(filas);
    }

    /**
     * Encabezado, línea separadora y filas, cada una terminada en salto de línea.
     */
    public String formatear() {
        int[] anchos = new int[encabezados.length];
        for (int i = 0; i < encabezados.length; i++) {
            anchos[i] = encabezados[i].length();
            for (String[] fila : filas) {
                anchos[i] = Math.max(anchos[i], valor(fila[i]).length());
            }
        }
        StringBuilder texto = new StringBuilder();
        texto.append(linea(encabezados, anchos)).append('\n');
        String[] separador = new String[encabezados.length];
        for (int i = 0; i < separador.length; i++) {
            separador[i] = "-".repeat(anchos[i]);
        }
        texto.append(linea(separador, anchos).replace(' ', '-').replace('|', '+')).append('\n');
        for (String[] fila : filas) {
            texto.append(linea(fila, anchos)).append('\n');
        }
        return texto.toString();
    }

    private String linea(String[] valores, int[] anchos) {
        StringBuilder linea = new StringBuilder();
        for (int i = 0; i < valores.length; i++) {
            if (i > 0) {
                linea.append(" | ");
            }
            String formato = alineadaDerecha[i] ? "%" + anchos[i] + "s" : "%-" + anchos[i] + "s";
            linea.append(String.format(formato, valor(valores[i])));
        }
        return linea.toString().stripTrailing();
    }

    private static String valor(String texto) {
        return texto == null ? "" : texto;
    }

    @Override
    public String toString() {
        return "TablaReporte{columnas=" + encabezados.length + ", filas=" + filas.size() + "}";
    }
}
