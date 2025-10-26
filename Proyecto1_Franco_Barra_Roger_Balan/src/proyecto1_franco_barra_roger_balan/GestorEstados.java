/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 * Clase GestorEstados: Maneja la lógica de transición entre los 6+ estados del proceso.
 * Centraliza las decisiones de movimiento de colas, haciendo el SO más limpio.
 */
public class GestorEstados {
    
    private final Cola readyQueue;
    private final Cola blockedQueue;
    private final Cola suspendedReadyQueue; 
    private final Cola suspendedBlockedQueue; 
    private final Scheduler scheduler;

    public GestorEstados(Cola readyQueue, Cola blockedQueue, 
                         Cola suspendedReadyQueue, Scheduler scheduler) {
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.suspendedReadyQueue = suspendedReadyQueue;
        this.scheduler = scheduler;
        this.suspendedBlockedQueue = new Cola(); 
    }
    
    public void moveBlockedToReady(PCB pcb) {
        pcb.setStatus(ProcessStatus.READY);
        scheduler.reinsertProcess(pcb); 
    }
    
    public boolean suspendReadyProcess(PCB pcb) {
        if (pcb.getStatus() != ProcessStatus.READY) return false;
        
        pcb.setStatus(ProcessStatus.SUSPENDED_READY);
        this.suspendedReadyQueue.agregar(pcb);
        return true;
    }
    
    public PCB resumeReadyProcess() {
        PCB pcb = this.suspendedReadyQueue.sacar();
        if (pcb != null) {
            moveBlockedToReady(pcb);
        }
        return pcb;
    }
    
    public boolean suspendBlockedProcess(PCB pcb) {
        if (pcb.getStatus() == ProcessStatus.BLOCKED) {
            pcb.setStatus(ProcessStatus.SUSPENDED_BLOCKED);
            this.suspendedBlockedQueue.agregar(pcb);
            return true;
        }
        return false;
    }

    public Cola getSuspendedReadyQueue() {
        return suspendedReadyQueue;
    }
    
    public Cola getSuspendedBlockedQueue() {
        return suspendedBlockedQueue;
    }
    
    public Cola getReadyQueue() {
        return readyQueue;
    }
    
    public Cola getBlockedQueue() {
        return blockedQueue;
    }
    
    public int getTotalProcesses() {
        return readyQueue.getSize() + blockedQueue.getSize() + 
               suspendedReadyQueue.getSize() + suspendedBlockedQueue.getSize();
    }
}