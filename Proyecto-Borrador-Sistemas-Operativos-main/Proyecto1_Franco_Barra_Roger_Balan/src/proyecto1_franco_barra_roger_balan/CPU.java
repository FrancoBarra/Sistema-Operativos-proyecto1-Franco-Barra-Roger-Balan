/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

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
     * Simula la ejecución de una instrucción (un ciclo de reloj).
     * @return El PCB desalojado si la ejecución termina, se bloquea, o expira el quantum,
     * o null si el proceso continúa en ejecución.
     */
    public PCB executeCycle() {
        if (currentProcess == null) {
            return null;
        }

        // 1. Ejecutar instrucción completa (PC++, MAR++, ciclosRestantes--)
        currentProcess.executeInstruction();
        this.quantumCounter++; // Se incrementa por cada ciclo de ejecución

        // 2. Revisar si Termina
        if (currentProcess.getCiclosRestantes() <= 0) {
            currentProcess.setStatus(ProcessStatus.TERMINATED);
            return endExecution();
        }

        // 3. Revisar si hay Solicitud de E/S (Excepción)
        if (currentProcess.getType() == ProcessType.IO_BOUND) {
             // Comprueba si el PC ha alcanzado un múltiplo del ciclo de excepción
             if (currentProcess.getProgramCounter() > 0 && 
                 currentProcess.getProgramCounter() % currentProcess.getCiclosParaExcepcion() == 0) {
                
                // Inicializa el contador de espera de E/S
                currentProcess.setCiclosIOEspera(currentProcess.getCiclosParaSatisfacerIO());
                currentProcess.setStatus(ProcessStatus.BLOCKED);
                return endExecution(); // Desalojo por E/S
            }
        }

        // 4. Revisar si hay Interrupción por Quantum (solo si el quantum se alcanzó)
        if (this.quantumCounter >= this.quantumSize) {
             // El estado permanece RUNNING. El SO detectará que fue desalojado (expelledPcb != null)
             // y lo cambiará a READY antes de reinsertarlo.
             return endExecution(); 
        }

        return null; // El proceso continúa en RUNNING
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
    
    public int getQuantumSize() {
        return quantumSize;
    }
}