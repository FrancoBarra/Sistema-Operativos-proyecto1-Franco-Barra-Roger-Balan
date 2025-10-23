/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
public class Proyecto1_Franco_Barra_Roger_Balan {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
        javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    }

    // --- 2. Crear y mostrar la GUI ---
    // Esto asegura que la GUI se cree en el hilo correcto de Swing
    java.awt.EventQueue.invokeLater(new Runnable() {
        public void run() {
            // (Asegúrate de importar tu clase GUI)
            new GUI().setVisible(true);
        }
    });
        
    }
    
}
