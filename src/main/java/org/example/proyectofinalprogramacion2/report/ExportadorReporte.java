package org.example.proyectofinalprogramacion2.report;

import org.example.proyectofinalprogramacion2.exception.ReporteException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Guarda el texto de un reporte en un archivo .txt (UTF-8).
 */
// SRP: solo escribe el archivo; el contenido lo genera cada ReporteExportable.
public class ExportadorReporte {

    private static final DateTimeFormatter FECHA_ARCHIVO = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmm");

    /**
     * Nombre sugerido: reporte_&lt;tipo&gt;_&lt;fecha&gt;.txt, por ejemplo reporte_ventas_2026-11-15_1030.txt.
     */
    public static String nombreSugerido(TipoReporte tipo, LocalDateTime ahora) {
        return "reporte_" + tipo.getClave() + "_" + FECHA_ARCHIVO.format(ahora) + ".txt";
    }

    /**
     * Guarda el texto en la carpeta indicada con el nombre sugerido.
     *
     * @return ruta del archivo creado
     */
    public Path exportar(String texto, Path carpeta, TipoReporte tipo, LocalDateTime ahora) throws ReporteException {
        Objects.requireNonNull(carpeta, "La carpeta es obligatoria");
        Objects.requireNonNull(tipo, "El tipo de reporte es obligatorio");
        Objects.requireNonNull(ahora, "La fecha es obligatoria");
        return guardar(texto, carpeta.resolve(nombreSugerido(tipo, ahora)));
    }

    /**
     * Guarda el texto en el archivo indicado (por ejemplo, el que el usuario eligió); lo reemplaza si existe.
     *
     * @return ruta del archivo creado
     */
    public Path guardar(String texto, Path archivo) throws ReporteException {
        Objects.requireNonNull(texto, "El texto del reporte es obligatorio");
        Objects.requireNonNull(archivo, "El archivo es obligatorio");
        try {
            Path carpeta = archivo.toAbsolutePath().getParent();
            if (carpeta != null) {
                Files.createDirectories(carpeta);
            }
            Files.writeString(archivo, texto, StandardCharsets.UTF_8);
            return archivo;
        } catch (IOException e) {
            throw new ReporteException("No se pudo guardar el reporte en " + archivo, e);
        }
    }
}
