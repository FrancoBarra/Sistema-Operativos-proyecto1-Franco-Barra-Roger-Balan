/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: Scheduler.java

/**
 * Clase Scheduler: Implementa y gestiona las políticas de planificación
 * utilizando su propia enumeración anidada.
 */
public class Scheduler {

    /**
     * Enumeración SchedulingAlgorithm: Define las 6 políticas de planificación requeridas.
     * Es estática para que pueda ser referenciada como Scheduler.SchedulingAlgorithm.
     */
    public static enum SchedulingAlgorithm {
        FCFS,           // First-Come, First-Served
        SJF_NON_PREEMPTIVE, // Shortest Job First (No Apropiativo)
        SJF_PREEMPTIVE,     // Shortest Remaining Time First (SRTF) (Apropiativo)
        ROUND_ROBIN,    // Round Robin
        PRIORITY_NON_PREEMPTIVE, // Prioridad (No Apropiativo)
        PRIORITY_PREEMPTIVE      // Prioridad (Apropiativo)
    }

    private Cola readyQueue;
    private SchedulingAlgorithm activeAlgorithm;
    private int timeQuantum; // Quantum de tiempo para Round Robin

    /**
     * Constructor
     * @param readyQueue La instancia de la Cola de Listos.
     * @param initialAlgorithm El algoritmo de planificación inicial.
     * @param quantum El quantum de tiempo.
     */
    public Scheduler(Cola readyQueue, SchedulingAlgorithm initialAlgorithm, int quantum) {
        this.readyQueue = readyQueue;
        this.activeAlgorithm = initialAlgorithm;
        this.timeQuantum = quantum;
    }

    /**
     * Método principal: Selecciona el próximo PCB para la CPU
     * basado en el algoritmo activo.
     * @return El PCB seleccionado para ejecución o null si la cola está vacía.
     */
    public PCB selectNextProcess() {
        if (readyQueue.isEmpty()) {
            return null;
        }

        // Usamos el switch para aplicar la lógica específica
        switch (activeAlgorithm) {
            case FCFS:
                return _selectFCFS();
            case SJF_NON_PREEMPTIVE:
            case SJF_PREEMPTIVE:
                return _selectSJF(); 
            case ROUND_ROBIN:
                return readyQueue.verFrente(); 
            case PRIORITY_NON_PREEMPTIVE:
            case PRIORITY_PREEMPTIVE:
                // Lógica de Prioridad: Asumimos FCFS por defecto hasta la implementación
                return _selectFCFS(); 
            default:
                return _selectFCFS(); 
        }
    }
    
    // --- Métodos de Implementación de Algoritmos (Stubs) ---
    
    private PCB _selectFCFS() {
        return readyQueue.verFrente(); 
    }
    
    private PCB _selectSJF() {
        // NOTA: Implementación real de SJF requiere iterar la Cola para encontrar el PCB
        // con el menor 'ciclosRestantes'. Por ahora, solo retorna el frente.
        return readyQueue.verFrente(); 
    }
    
    /**
     * Reinserta un proceso en la Cola de Listos después de desalojo o E/S.
     * @param pcb El proceso a reinsertar.
     */
    public void reinsertProcess(PCB pcb) {
        // Aseguramos que el estado sea 'READY' antes de reinsertar
        pcb.setStatus(ProcessStatus.READY); 
        
        if (activeAlgorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            // El proceso desalojado vuelve al final.
            readyQueue.agregar(pcb);
        } else {
            // Lógica de inserción ordenada (SJF, Prioridad) o al final (FCFS/Default)
            // Se asume FCFS hasta la implementación completa.
            readyQueue.agregar(pcb); 
        }
    }


    // --- Getters y Setters para configuración dinámica ---
    
    public void setActiveAlgorithm(SchedulingAlgorithm algorithm) {
        this.activeAlgorithm = algorithm;
    }

    public SchedulingAlgorithm getActiveAlgorithm() {
        return activeAlgorithm;
    }

    public int getTimeQuantum() {
        return timeQuantum;
    }

    public void setTimeQuantum(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }
}