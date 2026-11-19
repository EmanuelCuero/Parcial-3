package com.wotc.gremio.controlador;

import com.wotc.gremio.excepciones.AventureroDuplicadoException;
import com.wotc.gremio.excepciones.DatosInvalidosException;
import com.wotc.gremio.excepciones.DatosInvalidosException.Campo;
import com.wotc.gremio.excepciones.HistorialException;
import com.wotc.gremio.modelo.Aventurero;
import com.wotc.gremio.modelo.AventureroFactory;
import com.wotc.gremio.modelo.ClaseAventurero;
import com.wotc.gremio.modelo.ResultadoCombate;
import com.wotc.gremio.modelo.SimuladorCombate;
import com.wotc.gremio.modelo.TurnoCombate;
import com.wotc.gremio.excepciones.ArchivoGremioException;
import com.wotc.gremio.excepciones.FormatoArchivoException;
import com.wotc.gremio.persistencia.ArchivoGremio;
import com.wotc.gremio.persistencia.RegistroGremio;
import com.wotc.gremio.vista.VentanaGremio;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador del gremio: guarda a todos los aventureros en una única colección
 * polimórfica, aplica las reglas de negocio y atiende los eventos de la vista.
 */
public class GremioController {

    /** Colección polimórfica: cualquier subclase de Aventurero. */
    private final List<Aventurero> aventureros = new ArrayList<>();
    private final RegistroGremio registro;
    private final ArchivoGremio archivoGremio;
    private final SimuladorCombate simulador;
    private VentanaGremio vista;

    /** Controlador sin vista (lógica pura, usado en las pruebas). */
    public GremioController(RegistroGremio registro) {
        this(registro, new SimuladorCombate());
    }

    /**
     * Sobrecarga: permite inyectar el simulador (por ejemplo con dados fijos).
     * Los aventureros se guardan junto al historial.
     */
    public GremioController(RegistroGremio registro, SimuladorCombate simulador) {
        this(registro, simulador,
                new ArchivoGremio(registro.getArchivo().resolveSibling(ArchivoGremio.NOMBRE_ARCHIVO)));
    }

    /** Sobrecarga: indica también el archivo donde se guardan los aventureros. */
    public GremioController(RegistroGremio registro, SimuladorCombate simulador,
            ArchivoGremio archivoGremio) {
        if (registro == null || simulador == null || archivoGremio == null) {
            throw new IllegalArgumentException("El registro, el simulador y el archivo son obligatorios.");
        }
        this.registro = registro;
        this.simulador = simulador;
        this.archivoGremio = archivoGremio;
    }

    /** Sobrecarga: controlador conectado a la ventana. */
    public GremioController(VentanaGremio vista, RegistroGremio registro, ArchivoGremio archivoGremio) {
        this(registro, new SimuladorCombate(), archivoGremio);
        this.vista = vista;
        conectarVista();
        cargarDesdeVista();
    }

    /** Sobrecarga: ventana con el archivo de aventureros por defecto. */
    public GremioController(VentanaGremio vista, RegistroGremio registro) {
        this(vista, registro, new ArchivoGremio());
    }

    // ------------------------------------------------------------------
    // Lógica del gremio
    // ------------------------------------------------------------------

    /**
     * RF1 + RF4: agrega el aventurero, lo guarda en el archivo del gremio y
     * anexa el registro al historial. Si algo falla, el gremio queda como estaba.
     *
     * @throws AventureroDuplicadoException si ya hay un aventurero con ese nombre
     * @throws HistorialException           si no se pudo escribir el historial
     * @throws ArchivoGremioException       si no se pudo guardar el gremio
     */
    public void registrarAventurero(Aventurero aventurero)
            throws AventureroDuplicadoException, HistorialException, ArchivoGremioException {
        if (aventurero == null) {
            throw new IllegalArgumentException("El aventurero no puede ser nulo.");
        }
        if (existeAventurero(aventurero.getNombre())) {
            throw new AventureroDuplicadoException(aventurero.getNombre());
        }
        try {
            registro.registrarAventurero(aventurero);
        } catch (IOException e) {
            throw new HistorialException("guardar el registro", e);
        }
        aventureros.add(aventurero);
        try {
            guardarGremio();
        } catch (ArchivoGremioException e) {
            aventureros.remove(aventureros.size() - 1);
            throw e;
        }
    }

    /**
     * Carga los aventureros guardados en el archivo del gremio (por ejemplo al
     * abrir la aplicación). Los que ya estén en el gremio no se duplican.
     *
     * @return cuántos aventureros se cargaron
     * @throws ArchivoGremioException  si el archivo no se puede leer
     * @throws FormatoArchivoException si alguna línea está mal formada; en ese
     *                                 caso no se carga ningún aventurero
     */
    public int cargarGremio() throws ArchivoGremioException, FormatoArchivoException {
        List<Aventurero> guardados;
        try {
            guardados = archivoGremio.cargar();
        } catch (IOException e) {
            throw new ArchivoGremioException("leer", e);
        }
        int cargados = 0;
        for (Aventurero a : guardados) {
            if (!existeAventurero(a.getNombre())) {
                aventureros.add(a);
                cargados++;
            }
        }
        return cargados;
    }

    /** Sobrescribe el archivo del gremio con los aventureros actuales. */
    private void guardarGremio() throws ArchivoGremioException {
        try {
            archivoGremio.guardar(aventureros);
        } catch (IOException e) {
            throw new ArchivoGremioException("guardar", e);
        }
    }

    public ArchivoGremio getArchivoGremio() {
        return archivoGremio;
    }

    /** ¿Hay ya un aventurero con ese nombre? (sin distinguir mayúsculas) */
    public boolean existeAventurero(String nombre) {
        String buscado = nombre == null ? "" : nombre.trim();
        return aventureros.stream().anyMatch(a -> a.getNombre().equalsIgnoreCase(buscado));
    }

    /**
     * Expulsa del gremio al aventurero de la posición indicada.
     *
     * @return el aventurero expulsado
     */
    public Aventurero expulsarAventurero(int indice)
            throws HistorialException, ArchivoGremioException {
        if (indice < 0 || indice >= aventureros.size()) {
            throw new IllegalArgumentException("No existe un aventurero en la posición " + indice + ".");
        }
        Aventurero expulsado = aventureros.get(indice);
        try {
            registro.registrarExpulsion(expulsado);
        } catch (IOException e) {
            throw new HistorialException("guardar la expulsión", e);
        }
        aventureros.remove(indice);
        try {
            guardarGremio();
        } catch (ArchivoGremioException e) {
            aventureros.add(indice, expulsado);
            throw e;
        }
        return expulsado;
    }

    /** RF2: lista de solo lectura con los aventureros registrados. */
    public List<Aventurero> getAventureros() {
        return Collections.unmodifiableList(aventureros);
    }

    /**
     * RF3 + RF4: el gremio combate contra un enemigo acorde a su nivel. Se recorre
     * la colección invocando {@code simularCombate()} y {@code resolverTurno()} de
     * forma polimórfica (cada subclase responde con su táctica y sus dados) y se
     * anexa el resultado al historial.
     *
     * @throws IllegalStateException si el gremio está vacío
     */
    public ResultadoCombate simularBatalla() throws HistorialException {
        if (aventureros.isEmpty()) {
            throw new IllegalStateException("No hay aventureros en el gremio.");
        }
        ResultadoCombate combate = simulador.simular(aventureros);
        try {
            registro.registrarCombate(combate);
        } catch (IOException e) {
            throw new HistorialException("guardar el combate", e);
        }
        return combate;
    }

    /** Turnos del combate (vacío si no hay aventureros). */
    public List<TurnoCombate> simularTurnos() throws HistorialException {
        return aventureros.isEmpty() ? List.of() : simularBatalla().turnos();
    }

    /** Sobre {@link #simularTurnos()}: solo los mensajes de cada aventurero. */
    public List<String> simularCombate() throws HistorialException {
        return simularTurnos().stream().map(TurnoCombate::accion).toList();
    }

    /**
     * Lee el historial completo del gremio (registros, expulsiones y combates).
     *
     * @throws HistorialException si el archivo no se puede leer
     */
    public List<String> leerHistorial() throws HistorialException {
        try {
            return registro.leerHistorial();
        } catch (IOException e) {
            throw new HistorialException("leer los datos", e);
        }
    }

    public double getNivelPromedio() {
        return aventureros.stream().mapToInt(Aventurero::getNivel).average().orElse(0);
    }

    public int getPuntosVidaTotales() {
        return aventureros.stream().mapToInt(Aventurero::getPuntosVida).sum();
    }

    // ------------------------------------------------------------------
    // Eventos de la vista
    // ------------------------------------------------------------------

    private void conectarVista() {
        vista.getCmbClase().addActionListener(e -> vista.mostrarClase(claseSeleccionada()));
        vista.getBtnRegistrar().addActionListener(e -> registrarDesdeVista());
        vista.getBtnLimpiar().addActionListener(e -> {
            vista.limpiarFormulario();
            vista.mostrarEstado("Formulario limpio.");
        });
        vista.getBtnSimular().addActionListener(e -> simularDesdeVista());
        vista.getBtnExpulsar().addActionListener(e -> expulsarDesdeVista());
        vista.getBtnHistorial().addActionListener(e -> historialDesdeVista());
        vista.getTblGremio().getSelectionModel().addListSelectionListener(e ->
                vista.getBtnExpulsar().setEnabled(vista.getFilaSeleccionada() >= 0));
        vista.mostrarClase(claseSeleccionada());
        actualizarEstadisticas();
    }

    private void registrarDesdeVista() {
        ClaseAventurero clase = claseSeleccionada();
        try {
            Aventurero nuevo = clase.getTipoAtributo() == ClaseAventurero.TipoAtributo.BOOLEANO
                    ? AventureroFactory.crear(clase, vista.getNombreIngresado(),
                            vista.getNivelIngresado(), vista.getPuntosVidaIngresados(),
                            vista.isAtributoMarcado())
                    : AventureroFactory.crear(clase, vista.getNombreIngresado(),
                            vista.getNivelIngresado(), vista.getPuntosVidaIngresados(),
                            vista.getAtributoTexto());
            registrarAventurero(nuevo);
            vista.agregarFila(nuevo);
            actualizarEstadisticas();
            vista.limpiarFormulario();
            vista.mostrarExito(nuevo.describir() + " se ha unido al gremio.");
        } catch (DatosInvalidosException ex) {
            vista.mostrarErrorCampo(ex.getCampo(), ex.getMessage());
            vista.mostrarFallo("Revisa el dato marcado en rojo.");
        } catch (AventureroDuplicadoException ex) {
            vista.mostrarErrorCampo(Campo.NOMBRE, ex.getMessage());
            vista.mostrarFallo("Elige otro nombre: cada aventurero es único.");
        } catch (HistorialException | ArchivoGremioException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se registró el aventurero.");
        }
    }

    /** Al abrir la ventana, muestra los aventureros guardados en la sesión anterior. */
    private void cargarDesdeVista() {
        try {
            int cargados = cargarGremio();
            aventureros.forEach(vista::agregarFila);
            actualizarEstadisticas();
            if (cargados > 0) {
                vista.mostrarEstado("Se cargaron " + cargados + " aventureros desde "
                        + archivoGremio.getArchivo() + ".");
            }
        } catch (ArchivoGremioException | FormatoArchivoException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se pudo cargar el gremio guardado.");
        }
    }

    private void expulsarDesdeVista() {
        int fila = vista.getFilaSeleccionada();
        if (fila < 0) {
            return;
        }
        Aventurero elegido = aventureros.get(fila);
        if (!vista.confirmar("¿Expulsar a " + elegido.describir() + " del gremio?")) {
            return;
        }
        try {
            expulsarAventurero(fila);
            vista.quitarFila(fila);
            actualizarEstadisticas();
            vista.mostrarExito(elegido.describir() + " ha sido expulsado del gremio.");
        } catch (HistorialException | ArchivoGremioException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se expulsó al aventurero.");
        }
    }

    /** Abre la ventana con el historial leído de registro_gremio.txt. */
    private void historialDesdeVista() {
        try {
            List<String> lineas = leerHistorial();
            vista.mostrarHistorial(lineas, registro.getArchivo().toString());
            vista.mostrarEstado("Historial: " + lineas.size() + " líneas leídas de " + registro.getArchivo() + ".");
        } catch (HistorialException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se pudo leer el historial.");
        }
    }

    private void simularDesdeVista() {
        if (aventureros.isEmpty()) {
            vista.limpiarCombate();
            vista.mostrarFallo("El gremio está vacío: registra al menos un aventurero.");
            return;
        }
        try {
            ResultadoCombate combate = simularBatalla();
            vista.mostrarCombate(combate);
            if (combate.victoria()) {
                vista.mostrarExito(combate.resumen() + " Guardado en " + registro.getArchivo() + ".");
            } else {
                vista.mostrarEstado(combate.resumen() + " ¡Vuelve a intentarlo!");
            }
        } catch (HistorialException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se pudo guardar el combate.");
        }
    }

    private void actualizarEstadisticas() {
        vista.mostrarEstadisticas(aventureros.size(), getNivelPromedio(), getPuntosVidaTotales());
    }

    /** Traduce el texto del combo (definido en el diseñador) a la clase del modelo. */
    private ClaseAventurero claseSeleccionada() {
        String seleccion = vista.getClaseSeleccionada();
        return ClaseAventurero.desdeNombre(seleccion)
                .orElseThrow(() -> new IllegalStateException("Clase desconocida: " + seleccion));
    }
}
