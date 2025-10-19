/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: GestorEstados.java

/**
 * Clase GestorEstados: Maneja la lógica de transición entre los 6+ estados del proceso.
 * Centraliza las decisiones de movimiento de colas, haciendo el SO más limpio.
 */
public class GestorEstados {
    
    // Referencias a las colas que gestiona
    private final Cola readyQueue;
    private final Cola blockedQueue;
    private final Cola suspendedReadyQueue; 
    private final Cola suspendedBlockedQueue; 
    private final Scheduler scheduler;

    /**
     * Constructor
     */
    public GestorEstados(Cola readyQueue, Cola blockedQueue, 
                         Cola suspendedReadyQueue, Scheduler scheduler) {
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.suspendedReadyQueue = suspendedReadyQueue;
        this.scheduler = scheduler;
        // NOTA: suspendedBlockedQueue se manejaría si fuera necesario, por ahora se omite.
        this.suspendedBlockedQueue = new Cola(); // Instancia para completitud
    }
    
    /**
     * Mueve un proceso de cualquier estado de Bloqueo a Listo (Despertar).
     * Usado después de que una I/O termina o una suspensión se revierte.
     */
    public void moveBlockedToReady(PCB pcb) {
        pcb.setStatus(ProcessStatus.READY);
        // El scheduler se encarga de insertarlo en el lugar correcto (FCFS, SJF, etc.)
        scheduler.reinsertProcess(pcb); 
    }
    
    /**
     * Lógica de Suspensión: Mueve un proceso de READY a SUSPENDED_READY.
     * Esto simula la acción del Planificador a Mediano Plazo para liberar memoria.
     * @param pcb El PCB a suspender.
     */
    public boolean suspendReadyProcess(PCB pcb) {
        // Asumimos que el proceso fue removido de readyQueue antes de llamar.
        if (pcb.getStatus() != ProcessStatus.READY) return false;
        
        pcb.setStatus(ProcessStatus.SUSPENDED_READY);
        this.suspendedReadyQueue.agregar(pcb);
        return true;
    }
    
    /**
     * Lógica de Reanudación: Mueve un proceso de SUSPENDED_READY a READY.
     * @return El PCB reanudado o null si la cola está vacía.
     */
    public PCB resumeReadyProcess() {
        PCB pcb = this.suspendedReadyQueue.sacar();
        if (pcb != null) {
            pcb.setStatus(ProcessStatus.READY);
            this.scheduler.reinsertProcess(pcb); // Lo devuelve a la ReadyQueue
        }
        return pcb;
    }
    
    /**
     * Lógica de Suspensión de Bloqueados: Mueve un proceso de BLOCKED a SUSPENDED_BLOCKED.
     * (Simulación de Planificador a Mediano Plazo para liberar memoria)
     */
    public boolean suspendBlockedProcess(int pcbId) {
        PCB pcb = this.blockedQueue.removerPorId(pcbId);
        if (pcb != null) {
            pcb.setStatus(ProcessStatus.SUSPENDED_BLOCKED);
            this.suspendedBlockedQueue.agregar(pcb);
            return true;
        }
        return false;
    }

    // --- Métodos de utilidad para el SO ---
    
    public Cola getSuspendedReadyQueue() {
        return suspendedReadyQueue;
    }
    
    public Cola getSuspendedBlockedQueue() {
        return suspendedBlockedQueue;
    }
}