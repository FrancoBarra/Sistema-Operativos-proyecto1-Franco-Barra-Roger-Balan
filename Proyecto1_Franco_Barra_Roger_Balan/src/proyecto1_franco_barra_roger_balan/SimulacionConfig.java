/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;


public class SimulacionConfig {
    
    private final long cycleDuration;

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