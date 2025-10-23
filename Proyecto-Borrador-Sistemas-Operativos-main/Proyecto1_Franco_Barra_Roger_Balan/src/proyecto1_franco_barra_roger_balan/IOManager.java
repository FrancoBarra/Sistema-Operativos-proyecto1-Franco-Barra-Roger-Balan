/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

import java.util.concurrent.Semaphore;

/**
 * Clase IOManager: Simula el dispositivo de Entrada/Salida (E/S)
 * que atiende a los procesos en la cola de bloqueados. Se ejecuta
 * en un hilo separado (implements Runnable) para lograr concurrencia
 * con la CPU.
 */
public class IOManager implements Runnable {

    private final Cola blockedQueue;
    private final GestorEstados gestorEstados;
    // Semáforo compartido con SistemaOperativo: garantiza el acceso seguro a las colas.
    private final Semaphore cpuMutex; 
    private volatile boolean isRunning = true;
    // Duración de la pausa en milisegundos para simular la velocidad de I/O.
    private final long ioCycleDuration = 20; 

    /**
     * Constructor del IOManager.
     * @param blockedQueue La cola de procesos bloqueados.
     * @param gestorEstados Referencia para mover procesos a READY cuando la I/O termina.
     * @param cpuMutex El semáforo de exclusión mutua.
     */
    public IOManager(Cola blockedQueue, GestorEstados gestorEstados, Semaphore cpuMutex) {
        this.blockedQueue = blockedQueue;
        this.gestorEstados = gestorEstados;
        this.cpuMutex = cpuMutex;
    }

    /**
     * Hilo principal de ejecución del I/O Manager.
     */
    @Override
    public void run() {
        while (isRunning) {
            try {
                // 1. Pausa para simular el tiempo que toma la operación de I/O
                java.lang.Thread.sleep(ioCycleDuration); 
                
                // 2. Adquirir el Mutex: Sección Crítica (Acceso a colas compartidas)
                cpuMutex.acquire(); 
                
                // 3. Procesar la Cola de Bloqueados
                processBlockedQueue();
                
            } catch (InterruptedException e) {
                // Si el hilo es interrumpido (e.g., al detener la simulación)
                java.lang.Thread.currentThread().interrupt();
                break;
            } finally {
                // 4. Liberar el Mutex: Sección Crítica (Fin)
                cpuMutex.release(); 
            }
        }
    }
    
    /**
     * Recorre la cola de bloqueados, reduce el tiempo de espera de I/O,
     * y transfiere a READY los procesos que han terminado su E/S.
     */
    private void processBlockedQueue() {
        if (blockedQueue.isEmpty()) {
            return;
        }

        // Usamos una cola temporal para evitar modificar la blockedQueue
        // mientras la estamos iterando (recorriendo).
        Cola readyToMove = new Cola(); 
        
        // 1. Recorrer la cola y actualizar contadores
        Node current = blockedQueue.getHead();
        while (current != null) {
            PCB pcb = current.getPcb();
            
            if (pcb.getStatus() == ProcessStatus.BLOCKED) {
                
                int ciclosPendientes = pcb.getCiclosIOEspera();

                if (ciclosPendientes > 0) {
                    // Decrementa el tiempo restante de servicio de I/O
                    pcb.setCiclosIOEspera(ciclosPendientes - 1);
                    pcb.incrementarTiempoBloqueado(); // Acumula métricas
                } 
                
                if (pcb.getCiclosIOEspera() == 0) {
                    // La operación ha terminado, marcar para mover
                    readyToMove.agregar(pcb);
                }
            }
            
            current = current.getNext();
        }
        
        // 2. Mover procesos de BLOCKED a READY
        while (!readyToMove.isEmpty()) {
            PCB pcbToMove = readyToMove.sacar();
            
            // a) Remover de la cola de bloqueados (¡Crucial!)
            blockedQueue.removerPorId(pcbToMove.getId());
            
            // b) Mover a la Ready Queue usando el GestorEstados
            gestorEstados.moveBlockedToReady(pcbToMove);
            // El moveBlockedToReady ya cambia el estado a READY y llama a reinsertProcess.
        }
    }

    /**
     * Método para detener la ejecución del hilo de forma controlada.
     */
    public void stopRunning() {
        this.isRunning = false;
    }
}
