package com.wotc.gremio.vista;

import java.awt.Component;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Columna "Clase" de la tabla: insignia + nombre de la clase en su color.
 */
public class RenderizadorTablaClase extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionado,
            boolean conFoco, int fila, int columna) {
        super.getTableCellRendererComponent(tabla, valor, seleccionado, conFoco, fila, columna);
        String clase = String.valueOf(valor);
        setIcon(IconosClase.de(clase, 24));
        setIconTextGap(8);
        setFont(getFont().deriveFont(Font.BOLD));
        if (!seleccionado) {
            setForeground(IconosClase.color(clase));
        }
        setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        return this;
    }
}
