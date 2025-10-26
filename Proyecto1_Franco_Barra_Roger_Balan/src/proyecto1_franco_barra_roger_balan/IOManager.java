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
    private final Semaphore cpuMutex; 
    private volatile boolean isRunning = true;
    private final long ioCycleDuration = 20; 

    public IOManager(Cola blockedQueue, GestorEstados gestorEstados, Semaphore cpuMutex) {
        this.blockedQueue = blockedQueue;
        this.gestorEstados = gestorEstados;
        this.cpuMutex = cpuMutex;
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                java.lang.Thread.sleep(ioCycleDuration); 

                cpuMutex.acquire();

                processBlockedQueue();
                
            } catch (InterruptedException e) {
                java.lang.Thread.currentThread().interrupt();
                break;
            } finally {
                cpuMutex.release();
            }
        }
    }
    
    private void processBlockedQueue() {
        if (blockedQueue.isEmpty()) {
            return;
        }

        Cola readyToMove = new Cola();
        
        Node current = blockedQueue.getHead();
        
        while (current != null) {
            PCB pcb = current.getPcb();
            
            if (pcb.getStatus() == ProcessStatus.BLOCKED) {
                
                int ciclosPendientes = pcb.getCiclosIOEspera();

                if (ciclosPendientes > 0) {
                    pcb.setCiclosIOEspera(ciclosPendientes - 1);
                    pcb.incrementarTiempoBloqueado(); 
                } 
                
                if (pcb.getCiclosIOEspera() == 0) {
                    readyToMove.agregar(pcb);
                }
            }
            
            current = current.getNext();
        }
        
        while (!readyToMove.isEmpty()) {
            PCB pcbToMove = readyToMove.sacar();
            
            blockedQueue.removerPorId(pcbToMove.getId());
            
            gestorEstados.moveBlockedToReady(pcbToMove);
        }
    }

    public void stop() {
        this.isRunning = false;
    }
}