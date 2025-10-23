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
    
    // Referencias a las colas que gestiona
    private final Cola readyQueue;
    private final Cola blockedQueue;
    private final Cola suspendedReadyQueue; 
    private final Cola suspendedBlockedQueue; // Se inicializa internamente
    private final Scheduler scheduler;

    /**
     * Constructor
     * @param readyQueue Cola de Listos.
     * @param blockedQueue Cola de Bloqueados.
     * @param suspendedReadyQueue Cola de Listos Suspendidos.
     * @param scheduler Referencia al planificador para reinsertar.
     * * NOTA: La suspendedBlockedQueue se instancia dentro, asumiendo 4 argumentos de entrada.
     */
    public GestorEstados(Cola readyQueue, Cola blockedQueue, 
                         Cola suspendedReadyQueue, Scheduler scheduler) {
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.suspendedReadyQueue = suspendedReadyQueue;
        this.scheduler = scheduler;
        // Instancia la cola faltante. Esto resuelve el problema de la firma del constructor.
        this.suspendedBlockedQueue = new Cola(); 
    }
    
    /**
     * Mueve un proceso de Bloqueo a Listo (Despertar).
     * Usado por el IOManager al finalizar una E/S.
     * @param pcb El PCB a mover.
     */
    public void moveBlockedToReady(PCB pcb) {
        // No se necesita remover de blockedQueue aquí; el IOManager lo hace.
        
        // Cambia el estado a READY
        pcb.setStatus(ProcessStatus.READY); 
        
        // Reinserta el proceso en la ReadyQueue (el Scheduler decide dónde va)
        this.scheduler.reinsertProcess(pcb); 
    }
    
    // =========================================================================
    // --- Lógica de Suspensión (Planificador a Mediano Plazo) ---
    // =========================================================================

    /**
     * Lógica de Suspensión de Listos: Mueve un proceso de READY a SUSPENDED_READY.
     * @return true si el proceso fue suspendido.
     */
    public boolean suspendReadyProcess(int pcbId) {
        // Remover de readyQueue
        PCB pcb = this.readyQueue.removerPorId(pcbId);
        
        if (pcb != null) {
            pcb.setStatus(ProcessStatus.SUSPENDED_READY);
            this.suspendedReadyQueue.agregar(pcb);
            return true;
        }
        return false;
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
     * @return true si el proceso fue suspendido.
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
    
    /**
     * Lógica de Reanudación de Bloqueados: Mueve un proceso de SUSPENDED_BLOCKED a BLOCKED.
     * @return El PCB reanudado o null si la cola está vacía.
     */
    public PCB resumeBlockedProcess() {
        PCB pcb = this.suspendedBlockedQueue.sacar();
        if (pcb != null) {
            pcb.setStatus(ProcessStatus.BLOCKED);
            this.blockedQueue.agregar(pcb); // Lo devuelve a la BlockedQueue
        }
        return pcb;
    }
}