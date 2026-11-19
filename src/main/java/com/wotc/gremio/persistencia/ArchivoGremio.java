package com.wotc.gremio.persistencia;

import com.wotc.gremio.excepciones.DatosInvalidosException;
import com.wotc.gremio.excepciones.FormatoArchivoException;
import com.wotc.gremio.modelo.Aventurero;
import com.wotc.gremio.modelo.AventureroFactory;
import com.wotc.gremio.modelo.ClaseAventurero;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Guarda los aventureros del gremio en un archivo de texto y los vuelve a
 * cargar al abrir la aplicación, para que no se pierdan al cerrarla.
 *
 * <p>Cada línea tiene el formato {@code Clase;Nombre;Nivel;PuntosVida;Atributo},
 * por ejemplo {@code Mago;Elminster;12;61;9}. Las líneas vacías y las que
 * empiezan por {@code #} se ignoran.</p>
 */
public class ArchivoGremio {

    public static final String NOMBRE_ARCHIVO = "gremio.txt";

    private static final String SEPARADOR = ";";
    private static final int CAMPOS = 5;

    private final Path archivo;

    /** Usa {@value #NOMBRE_ARCHIVO} en la carpeta de trabajo. */
    public ArchivoGremio() {
        this(Paths.get(NOMBRE_ARCHIVO));
    }

    /** Sobrecarga: permite indicar otro archivo (útil en las pruebas). */
    public ArchivoGremio(Path archivo) {
        if (archivo == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula.");
        }
        this.archivo = archivo;
    }

    public Path getArchivo() {
        return archivo;
    }

    /**
     * Escribe todos los aventureros, reemplazando el contenido anterior.
     * El flujo se cierra solo gracias a try-with-resources.
     */
    public void guardar(List<? extends Aventurero> aventureros) throws IOException {
        try (BufferedWriter escritor = Files.newBufferedWriter(archivo, StandardCharsets.UTF_8)) {
            escritor.write("# Gremio de aventureros: Clase;Nombre;Nivel;PuntosVida;Atributo");
            escritor.newLine();
            for (Aventurero a : aventureros) {
                escritor.write(aLinea(a));
                escritor.newLine();
            }
        }
    }

    /**
     * Lee los aventureros guardados. Si el archivo todavía no existe (primera
     * ejecución) devuelve una lista vacía.
     *
     * @throws FormatoArchivoException si alguna línea está mal formada
     * @throws IOException             si el archivo no se puede leer
     */
    public List<Aventurero> cargar() throws IOException, FormatoArchivoException {
        List<Aventurero> cargados = new ArrayList<>();
        if (Files.notExists(archivo)) {
            return cargados;
        }
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            int numero = 0;
            while ((linea = lector.readLine()) != null) {
                numero++;
                String limpia = linea.strip();
                if (!limpia.isEmpty() && !limpia.startsWith("#")) {
                    cargados.add(desdeLinea(limpia, numero));
                }
            }
        }
        return cargados;
    }

    /** Convierte un aventurero en una línea del archivo. */
    static String aLinea(Aventurero a) {
        return String.join(SEPARADOR, a.getClase(), a.getNombre(),
                String.valueOf(a.getNivel()), String.valueOf(a.getPuntosVida()),
                a.getValorAtributo());
    }

    /** Reconstruye un aventurero a partir de una línea del archivo. */
    static Aventurero desdeLinea(String linea, int numero) throws FormatoArchivoException {
        String[] campos = linea.split(SEPARADOR, -1);
        if (campos.length != CAMPOS) {
            throw new FormatoArchivoException(numero,
                    "se esperaban " + CAMPOS + " campos separados por ';' y hay " + campos.length + ".");
        }
        ClaseAventurero clase = ClaseAventurero.desdeNombre(campos[0].strip())
                .orElseThrow(() -> new FormatoArchivoException(numero,
                        "clase desconocida \"" + campos[0] + "\"."));
        try {
            String nombre = campos[1];
            int nivel = Integer.parseInt(campos[2].strip());
            int puntosVida = Integer.parseInt(campos[3].strip());
            String atributo = campos[4].strip();
            if (clase.getTipoAtributo() == ClaseAventurero.TipoAtributo.BOOLEANO) {
                return AventureroFactory.crear(clase, nombre, nivel, puntosVida, aBooleano(atributo, numero));
            }
            return AventureroFactory.crear(clase, nombre, nivel, puntosVida, atributo);
        } catch (NumberFormatException e) {
            throw new FormatoArchivoException(numero, "el nivel y los puntos de vida deben ser números.", e);
        } catch (DatosInvalidosException e) {
            throw new FormatoArchivoException(numero, e.getMessage(), e);
        }
    }

    private static boolean aBooleano(String texto, int numero) throws FormatoArchivoException {
        if ("true".equalsIgnoreCase(texto)) {
            return true;
        }
        if ("false".equalsIgnoreCase(texto)) {
            return false;
        }
        throw new FormatoArchivoException(numero, "el atributo debe ser true o false.");
    }
}
