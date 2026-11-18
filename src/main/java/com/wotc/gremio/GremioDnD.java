package com.wotc.gremio;

import com.wotc.gremio.controlador.GremioController;
import com.wotc.gremio.persistencia.RegistroGremio;
import com.wotc.gremio.vista.VentanaGremio;

/**
 * Punto de entrada: crea la vista, la persistencia y el controlador que los une.
 */
public class GremioDnD {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            // Si Nimbus no está disponible se usa el aspecto por defecto.
        }

        java.awt.EventQueue.invokeLater(() -> {
            VentanaGremio vista = new VentanaGremio();
            new GremioController(vista, new RegistroGremio());
            vista.setVisible(true);
        });
    }
}
