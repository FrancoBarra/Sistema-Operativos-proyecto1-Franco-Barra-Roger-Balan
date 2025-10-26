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

    public IOThread(PCB pcb, SistemaOperativo so) {
        this.pcb = pcb;
        this.sistemaOperativo = so;
    }
    
    @Override
    public void run() {
        synchronized(lock) {
            if (pcb.getStatus() != ProcessStatus.BLOCKED || pcb.getCiclosIOEspera() <= 0) {
                return;
            }
            
            int ciclosRestantesIO = pcb.getCiclosParaSatisfacerIO();
            
            while (isRunning && ciclosRestantesIO > 0) {
                try {
                    lock.wait(50); 
                    ciclosRestantesIO--;
                    pcb.setCiclosIOEspera(ciclosRestantesIO);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    isRunning = false;
                    break;
                }
            }
            
            if (isRunning) {
                sistemaOperativo.processIOCompleted(pcb.getId());
            }
        }
    }
    
    public void stopRunning() {
        synchronized(lock) {
            this.isRunning = false;
            lock.notify(); 
        }
    }
    
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