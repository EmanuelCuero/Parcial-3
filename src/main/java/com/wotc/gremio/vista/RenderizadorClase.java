package com.wotc.gremio.vista;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * Muestra cada clase del combo con su insignia.
 */
public class RenderizadorClase extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
            boolean seleccionado, boolean conFoco) {
        super.getListCellRendererComponent(lista, valor, indice, seleccionado, conFoco);
        setIcon(IconosClase.de(String.valueOf(valor), 24));
        setIconTextGap(8);
        setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
        return this;
    }
}
