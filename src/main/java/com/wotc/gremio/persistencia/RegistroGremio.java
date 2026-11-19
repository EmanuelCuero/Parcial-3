package com.wotc.gremio.persistencia;

import com.wotc.gremio.modelo.Aventurero;
import com.wotc.gremio.modelo.Enemigo;
import com.wotc.gremio.modelo.ResultadoCombate;
import com.wotc.gremio.modelo.TurnoCombate;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Anexa el historial del gremio (registros y combates) a un archivo de texto plano
 * y permite leerlo de nuevo.
 */
public class RegistroGremio {

    public static final String NOMBRE_ARCHIVO = "registro_gremio.txt";

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path archivo;

    /** Usa {@value #NOMBRE_ARCHIVO} en la carpeta de trabajo. */
    public RegistroGremio() {
        this(Paths.get(NOMBRE_ARCHIVO));
    }

    /** Sobrecarga: permite indicar otro archivo (útil en las pruebas). */
    public RegistroGremio(Path archivo) {
        if (archivo == null) {
            throw new IllegalArgumentException("La ruta del historial no puede ser nula.");
        }
        this.archivo = archivo;
    }

    public Path getArchivo() {
        return archivo;
    }

    /** Anexa el registro de un nuevo aventurero. */
    public void registrarAventurero(Aventurero aventurero) throws IOException {
        anexar("[" + ahora() + "] REGISTRO: " + aventurero.describir(true)
                + System.lineSeparator());
    }

    /** Anexa la salida (expulsión) de un aventurero del gremio. */
    public void registrarExpulsion(Aventurero aventurero) throws IOException {
        anexar("[" + ahora() + "] EXPULSIÓN: " + aventurero.describir()
                + System.lineSeparator());
    }

    /**
     * Sobrecarga: anexa un combate con dados, incluyendo enemigo, iniciativa,
     * tiradas de cada turno y el desenlace.
     */
    public void registrarCombate(ResultadoCombate combate) throws IOException {
        String salto = System.lineSeparator();
        Enemigo enemigo = combate.enemigo();
        StringBuilder texto = new StringBuilder();
        texto.append("[").append(ahora()).append("] SIMULACIÓN DE COMBATE (")
                .append(combate.turnos().size()).append(" aventureros) contra ")
                .append(enemigo.nombre()).append(" (CA ").append(enemigo.claseArmadura())
                .append(", ").append(enemigo.puntosVida()).append(" PV)").append(salto);
        for (TurnoCombate turno : combate.turnos()) {
            texto.append("    - [Iniciativa ").append(turno.iniciativa()).append("] ")
                    .append(turno.accion()).append(salto)
                    .append("        Tiradas: ").append(turno.resultado().detalle()).append(salto);
        }
        texto.append("    RESULTADO: ").append(combate.resumen()).append(salto);
        anexar(texto.toString());
    }

    /** Anexa el resultado completo de una simulación de combate. */
    public void registrarCombate(List<String> acciones) throws IOException {
        StringBuilder texto = new StringBuilder();
        texto.append("[").append(ahora()).append("] SIMULACIÓN DE COMBATE (")
                .append(acciones.size()).append(" aventureros)")
                .append(System.lineSeparator());
        for (String accion : acciones) {
            texto.append("    - ").append(accion).append(System.lineSeparator());
        }
        anexar(texto.toString());
    }

    /**
     * Lee el historial completo, línea por línea. Si todavía no se ha escrito
     * nada devuelve una lista vacía. El lector se cierra con try-with-resources.
     */
    public List<String> leerHistorial() throws IOException {
        List<String> lineas = new ArrayList<>();
        if (Files.notExists(archivo)) {
            return lineas;
        }
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return lineas;
    }

    private void anexar(String texto) throws IOException {
        Files.writeString(archivo, texto, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static String ahora() {
        return LocalDateTime.now().format(FORMATO_FECHA);
    }
}
