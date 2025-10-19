/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: Thread.java

/**
 * Clase Thread: Extiende la clase base de concurrencia de Java para simular 
 * la ejecución de un proceso. En esta arquitectura, se usa SOLO para simular 
 * el tiempo que tarda la operación de Entrada/Salida (E/S) de forma concurrente.
 */
public class Thread extends java.lang.Thread {

    private final PCB pcb;
    // Se necesita una referencia al SO para notificar cuando la E/S termine.
    private final SistemaOperativo sistemaOperativo; 
    
    private volatile boolean isRunning = true; 

    /**
     * Constructor
     * @param pcb El PCB que este hilo representa.
     * @param so La instancia del Sistema Operativo (Orquestador) para notificaciones.
     */
    public Thread(PCB pcb, SistemaOperativo so) {
        this.pcb = pcb;
        this.sistemaOperativo = so;
        setName("Thread-" + pcb.getId());
    }
    
    /**
     * El método run() contiene el código que se ejecutará en este hilo de E/S.
     * Se ejecuta SOLAMENTE cuando el proceso pasa al estado BLOCKED.
     */
    @Override
    public void run() {
        // Solo simula el bucle si el proceso está en BLOCKED y aún tiene ciclos de E/S
        if (pcb.getStatus() == ProcessStatus.BLOCKED && pcb.getCiclosIOEspera() > 0) {
            
            // Usamos los ciclosIOEspera del PCB para controlar el tiempo.
            int ciclosRestantesIO = pcb.getCiclosParaSatisfacerIO();
            
            while (isRunning && ciclosRestantesIO > 0) {
                
                ciclosRestantesIO--; 
                pcb.setCiclosIOEspera(ciclosRestantesIO); // Actualizamos el PCB
                
                // Ceder tiempo: Permite que otros hilos (incluido el hilo principal del SO) se ejecuten.
                java.lang.Thread.yield(); 
            }
            
            // *** Notificación al Sistema Operativo ***
            if (isRunning) {
                // La E/S ha terminado. El hilo notifica al SO.
                // El SO se encargará de mover el PCB de BLOCKED a READY.
                sistemaOperativo.processIOCompleted(this.pcb.getId());
            }
        }
    }
    
    /**
     * Detiene la ejecución del hilo de forma controlada.
     */
    public void stopRunning() {
        this.isRunning = false;
        // Intenta interrumpir el hilo si está bloqueado, aunque yield() no se bloquea.
        this.interrupt(); 
    }
    
    // Getter para que el SO pueda acceder al PCB
    public PCB getPcb() {
        return pcb;
    }
}
