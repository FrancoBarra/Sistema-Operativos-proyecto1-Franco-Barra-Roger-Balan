/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: CPU.java

/**
 * Clase CPU: Simula el hardware del procesador. Es un recurso pasivo que
 * ejecuta el PCB actualmente despachado y rastrea el quantum de tiempo.
 */
public class CPU {

    private PCB currentProcess; // El PCB actualmente en el registro de la CPU
    private int quantumCounter;  // Contador de ciclos para Round Robin/Tiempo de ejecución
    private final int quantumSize; // Tamaño del Quantum (configurado por el SO)

    /**
     * Constructor
     * @param quantumSize El tamaño del quantum para Round Robin.
     */
    public CPU(int quantumSize) {
        this.currentProcess = null;
        this.quantumCounter = 0;
        this.quantumSize = quantumSize;
    }

    /**
     * Coloca un PCB en el registro de la CPU para ejecución (Dispatch).
     */
    public void dispatch(PCB pcb) {
        this.currentProcess = pcb;
        this.currentProcess.setStatus(ProcessStatus.RUNNING);
        this.quantumCounter = 0; // Reinicia el contador para el nuevo proceso
    }

    /**
     * Ejecuta una unidad de trabajo (un ciclo de reloj).
     * @return El PCB que debe salir de la CPU (por interrupción, E/S, o terminación), o null.
     */
    public PCB executeCycle() {
        if (currentProcess == null) {
            return null; // CPU Inactiva
        }

        // 1. Simular Ejecución
        currentProcess.executeInstruction();
        this.quantumCounter++;

        // 2. Revisar si hay Terminación
        if (currentProcess.getCiclosRestantes() <= 0) {
            currentProcess.setStatus(ProcessStatus.TERMINATED);
            return endExecution();
        }

        // 3. Revisar si hay Solicitud de E/S
        if (currentProcess.getType() == ProcessType.IO_BOUND) {
             if (currentProcess.getProgramCounter() > 0 && 
                 currentProcess.getProgramCounter() % currentProcess.getCiclosParaExcepcion() == 0) {
                
                currentProcess.setStatus(ProcessStatus.BLOCKED);
                return endExecution(); // Desalojo por E/S
            }
        }

        // 4. Revisar si hay Interrupción por Quantum (solo si el quantum se alcanzó)
        if (this.quantumCounter >= this.quantumSize) {
             // El SO/Scheduler debe manejar esto. Aquí solo indicamos que el tiempo ha expirado.
             return endExecution(); 
        }

        return null; // El proceso continúa
    }
    
    /**
     * Proceso de desalojo. Libera la CPU y retorna el PCB.
     * @return El PCB desalojado.
     */
    private PCB endExecution() {
        PCB expelledPcb = this.currentProcess;
        this.currentProcess = null; // Liberar la CPU
        this.quantumCounter = 0;
        return expelledPcb;
    }
    
    // --- Getters ---
    public PCB getCurrentProcess() {
        return currentProcess;
    }
    
    public boolean isBusy() {
        return currentProcess != null;
    }
    
    public int getQuantumCounter() {
        return quantumCounter;
    }
}