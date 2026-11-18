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
    private final SimuladorCombate simulador;
    private VentanaGremio vista;

    /** Controlador sin vista (lógica pura, usado en las pruebas). */
    public GremioController(RegistroGremio registro) {
        this(registro, new SimuladorCombate());
    }

    /** Sobrecarga: permite inyectar el simulador (por ejemplo con dados fijos). */
    public GremioController(RegistroGremio registro, SimuladorCombate simulador) {
        this.registro = registro;
        this.simulador = simulador;
    }

    /** Sobrecarga: controlador conectado a la ventana. */
    public GremioController(VentanaGremio vista, RegistroGremio registro) {
        this(registro);
        this.vista = vista;
        conectarVista();
    }

    // ------------------------------------------------------------------
    // Lógica del gremio
    // ------------------------------------------------------------------

    /**
     * RF1 + RF4: agrega el aventurero y anexa el registro al historial.
     * Solo se agrega si el historial se pudo escribir.
     *
     * @throws AventureroDuplicadoException si ya hay un aventurero con ese nombre
     * @throws HistorialException           si no se pudo escribir el archivo
     */
    public void registrarAventurero(Aventurero aventurero)
            throws AventureroDuplicadoException, HistorialException {
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
    public Aventurero expulsarAventurero(int indice) throws HistorialException {
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
        } catch (HistorialException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se registró el aventurero.");
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
        } catch (HistorialException ex) {
            vista.mostrarError(ex.getMessage());
            vista.mostrarFallo("No se expulsó al aventurero.");
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
