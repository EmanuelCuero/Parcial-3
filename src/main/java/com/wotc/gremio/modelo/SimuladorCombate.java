package com.wotc.gremio.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Simula un combate del gremio contra un enemigo acorde a su nivel:
 * tira iniciativa, ordena los turnos y resuelve la táctica de cada aventurero
 * de forma polimórfica ({@code simularCombate()} y {@code resolverTurno()}).
 */
public class SimuladorCombate {

    private final Dados dados;

    public SimuladorCombate() {
        this(new Dados());
    }

    /** Sobrecarga: permite usar dados con semilla o trucados (pruebas). */
    public SimuladorCombate(Dados dados) {
        this.dados = dados;
    }

    public ResultadoCombate simular(List<? extends Aventurero> aventureros) {
        double nivelPromedio = aventureros.stream().mapToInt(Aventurero::getNivel).average().orElse(1);
        return simular(aventureros, Enemigo.segunNivel(nivelPromedio));
    }

    /** Sobrecarga: combate contra un enemigo concreto. */
    public ResultadoCombate simular(List<? extends Aventurero> aventureros, Enemigo enemigo) {
        record Participante(Aventurero aventurero, int iniciativa, int orden) {
        }

        // 1. Cada aventurero tira iniciativa (d20 + bonificador de su clase).
        List<Participante> participantes = new ArrayList<>();
        for (int i = 0; i < aventureros.size(); i++) {
            Aventurero a = aventureros.get(i);
            participantes.add(new Participante(a, dados.tirar(20) + a.getBonificadorIniciativa(), i));
        }
        // 2. Actúa primero la iniciativa más alta; en empate, quien se registró antes.
        participantes.sort(Comparator.comparingInt(Participante::iniciativa).reversed()
                .thenComparingInt(Participante::orden));

        // 3. Cada uno ejecuta su táctica (polimorfismo) y se descuenta el daño.
        List<TurnoCombate> turnos = new ArrayList<>();
        int vidaEnemigo = enemigo.puntosVida();
        for (Participante p : participantes) {
            Aventurero a = p.aventurero();
            ResultadoTurno resultado = a.resolverTurno(dados, enemigo);
            vidaEnemigo = Math.max(0, vidaEnemigo - resultado.danio());
            turnos.add(new TurnoCombate(turnos.size() + 1, a, a.simularCombate(),
                    p.iniciativa(), resultado, vidaEnemigo));
        }
        return new ResultadoCombate(enemigo, turnos);
    }
}
