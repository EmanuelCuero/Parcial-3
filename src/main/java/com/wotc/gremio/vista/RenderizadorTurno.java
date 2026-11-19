package com.wotc.gremio.vista;

import com.wotc.gremio.modelo.Aventurero;
import com.wotc.gremio.modelo.ResultadoTurno;
import com.wotc.gremio.modelo.TurnoCombate;
import java.awt.Color;
import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.SwingConstants;

/**
 * Dibuja cada turno del combate como una tarjeta: insignia de la clase,
 * franja de color a la izquierda, título y descripción de la táctica.
 */
public class RenderizadorTurno extends DefaultListCellRenderer {

    private static final Color FONDO_PAR = Color.WHITE;
    private static final Color FONDO_IMPAR = new Color(247, 248, 252);
    private static final Color SEPARADOR = new Color(229, 231, 235);
    private static final int MARGEN_TEXTO = 110;

    @Override
    public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
            boolean seleccionado, boolean conFoco) {
        super.getListCellRendererComponent(lista, valor, indice, false, false);
        if (!(valor instanceof TurnoCombate turno)) {
            return this;
        }
        Aventurero a = turno.aventurero();
        Color color = IconosClase.color(a.getClase());
        int ancho = Math.max(220, lista.getParent().getWidth() - MARGEN_TEXTO);

        setIcon(IconosClase.de(a.getClase(), 32));
        setIconTextGap(12);
        setVerticalTextPosition(SwingConstants.CENTER);
        ResultadoTurno resultado = turno.resultado();
        String efecto = resultado.danio() > 0
                ? "<b style='color:#B23A48'>" + resultado.danio() + " de daño</b>"
                : resultado.curacion() > 0
                ? "<b style='color:#2E8B6E'>+" + resultado.curacion() + " PV curados</b>"
                : a.esApoyo()
                ? "<b style='color:#6C4AB6'>apoyo</b>"
                : "<b style='color:#6B7280'>sin daño</b>";
        setText("<html><div style='width:" + ancho + "px'>"
                + "<b style='color:" + hex(color) + "'>Turno " + turno.numero() + " · "
                + escapar(a.describir()) + "</b>"
                + "<span style='color:#6B7280'> &nbsp;Iniciativa " + turno.iniciativa()
                + " · Nv " + a.getNivel() + " · " + a.getPuntosVida() + " PV</span>"
                + " &nbsp;" + efecto
                + "<br>" + escapar(turno.accion())
                + "<br><span style='color:#2A3358; font-size:90%'>Dados: "
                + escapar(resultado.detalle()) + "</span>"
                + "<br><span style='color:#6B7280; font-size:90%'>Vida del enemigo tras el turno: "
                + turno.vidaEnemigo() + " PV</span></div></html>");
        setBackground(indice % 2 == 0 ? FONDO_PAR : FONDO_IMPAR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, SEPARADOR),
                        BorderFactory.createMatteBorder(0, 5, 0, 0, color)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return this;
    }

    private static String hex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
