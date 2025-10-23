/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
public class IOThread implements Runnable {
    
    private final PCB pcb;
    private final SistemaOperativo sistemaOperativo;
    private volatile boolean isRunning = true;
    private final Object lock = new Object();

    /**
     * Constructor
     */
    public IOThread(PCB pcb, SistemaOperativo so) {
        this.pcb = pcb;
        this.sistemaOperativo = so;
    }
    
    /**
     * Método run() - Se ejecuta cuando el hilo inicia
     * Simula el tiempo de espera para operaciones de E/S
     */
    @Override
    public void run() {
        synchronized(lock) {
            // Verificar que el proceso esté en estado BLOCKED y tenga ciclos de E/S
            if (pcb.getStatus() != ProcessStatus.BLOCKED || pcb.getCiclosIOEspera() <= 0) {
                return;
            }
            
            int ciclosRestantesIO = pcb.getCiclosParaSatisfacerIO();
            
            while (isRunning && ciclosRestantesIO > 0) {
                try {
                    // Simular el paso del tiempo para la E/S
                    lock.wait(50); // Pequeña pausa para no consumir muchos recursos
                    ciclosRestantesIO--;
                    pcb.setCiclosIOEspera(ciclosRestantesIO);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    isRunning = false;
                    break;
                }
            }
            
            // Notificar al SO cuando la E/S termine
            if (isRunning) {
                sistemaOperativo.processIOCompleted(pcb.getId());
            }
        }
    }
    
    /**
     * Detiene la ejecución del hilo de forma controlada
     */
    public void stopRunning() {
        synchronized(lock) {
            this.isRunning = false;
            lock.notify(); // Despertar el hilo si está esperando
        }
    }
    
    // Getter para el PCB
    public PCB getPcb() {
        return pcb;
    }

    public boolean isIsRunning() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
}