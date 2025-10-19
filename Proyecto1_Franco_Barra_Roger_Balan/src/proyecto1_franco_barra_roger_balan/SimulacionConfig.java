/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: SimulacionConfig.java

/**
 * Clase SimulacionConfig: Contenedor simple para los datos leídos del archivo.
 * Almacena la duración del ciclo y los procesos iniciales en un array de Java.
 */
public class SimulacionConfig {
    
    private final long cycleDuration;
    // Usamos array simple de Java (permitido)
    private final PCB[] initialProcesses; 

    public SimulacionConfig(long cycleDuration, PCB[] initialProcesses) {
        this.cycleDuration = cycleDuration;
        this.initialProcesses = initialProcesses;
    }

    // --- Getters ---
    public long getCycleDuration() {
        return cycleDuration;
    }

    public PCB[] getInitialProcesses() {
        return initialProcesses;
    }
}