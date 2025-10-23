/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 * Clase Scheduler: Implementa y gestiona las políticas de planificación.
 */
public class Scheduler {

    /**
     * Enumeración SchedulingAlgorithm: Define las 7 políticas de planificación.
     */
    public static enum SchedulingAlgorithm {
        FCFS,           // First-Come, First-Served
        SJF_NON_PREEMPTIVE, // Shortest Job First (No Apropiativo)
        SJF_PREEMPTIVE,     // Shortest Remaining Time First (SRTF) (Apropiativo)
        ROUND_ROBIN,    // Round Robin
        PRIORITY_NON_PREEMPTIVE, // Prioridad (No Apropiativo)
        PRIORITY_PREEMPTIVE,     // Prioridad (Apropiativo)
        HRRN           // Highest Response Ratio Next
    }

    private final Cola readyQueue;
    private SchedulingAlgorithm activeAlgorithm;
    private final int timeQuantum; // Quantum de tiempo para Round Robin

    /**
     * Constructor
     * @param readyQueue La instancia de la Cola de Listos.
     * @param initialAlgorithm El algoritmo inicial.
     * @param quantumSize El tamaño del quantum.
     */
    public Scheduler(Cola readyQueue, SchedulingAlgorithm initialAlgorithm, int quantumSize) {
        this.readyQueue = readyQueue;
        this.activeAlgorithm = initialAlgorithm;
        this.timeQuantum = quantumSize;
    }

    /**
     * Método principal para seleccionar el próximo PCB a despachar a la CPU.
     * @param globalClock El ciclo actual del SO (necesario para HRRN).
     * @return El PCB seleccionado y *removido* de la cola de listos, o null.
     */
    public PCB schedule(long globalClock) {
        if (readyQueue.isEmpty()) {
            return null;
        }

        PCB selectedPcb = null;

        switch (activeAlgorithm) {
            case FCFS:
            case ROUND_ROBIN:
                // FCFS/Round Robin: Sacar el proceso del frente (FIFO)
                selectedPcb = readyQueue.sacar(); 
                break;

            case SJF_NON_PREEMPTIVE:
            case SJF_PREEMPTIVE: 
            case PRIORITY_NON_PREEMPTIVE:
            case PRIORITY_PREEMPTIVE:
            case HRRN:
                // Algoritmos que requieren buscar, encontrar y remover.
                selectedPcb = _selectAdvancedPolicy(globalClock);
                break;
        }
        
        return selectedPcb;
    }

    /**
     * Lógica para SJF/SRT, HRRN, y Prioridad.
     * Itera la cola para encontrar el mejor PCB, lo remueve de la cola y lo retorna.
     */
    private PCB _selectAdvancedPolicy(long globalClock) {
        PCB bestPcb = null;
        // Se usa INT_MAX para buscar el menor (tiempo), y -1.0 para buscar el mayor (ratio).
        int bestMetric = Integer.MAX_VALUE; 
        double bestRatio = -1.0; 

        Node current = readyQueue.getHead();

        while (current != null) {
            PCB pcb = current.getPcb();

            switch (activeAlgorithm) {
                case SJF_NON_PREEMPTIVE:
                case SJF_PREEMPTIVE:
                    // Criterio: Menor cantidad de ciclos restantes (Shortest Remaining Time)
                    if (pcb.getCiclosRestantes() < bestMetric) {
                        bestMetric = pcb.getCiclosRestantes();
                        bestPcb = pcb;
                    }
                    break;
                case PRIORITY_NON_PREEMPTIVE:
                case PRIORITY_PREEMPTIVE:
                    // Criterio: Mayor prioridad (asumiendo que un valor *más alto* es mejor)
                    if (pcb.getPriority() > bestMetric) {
                        bestMetric = pcb.getPriority();
                        bestPcb = pcb;
                    }
                    break;
                case HRRN:
                    // Criterio: Mayor Ratio de Respuesta (R = (W + S) / S)
                    
                    // W (Waiting Time) = Ciclo Actual - Tiempo Llegada - Tiempo Ejecutado
                    long waitingTime = globalClock - pcb.getArrivalTime() - pcb.getTiempoEnCPUAcumulado();
                    int ciclosRestantes = pcb.getCiclosRestantes(); 
                    
                    if (ciclosRestantes > 0) {
                        double responseRatio = (double)(waitingTime + ciclosRestantes) / ciclosRestantes;
                        
                        if (responseRatio > bestRatio) {
                            bestRatio = responseRatio;
                            bestPcb = pcb;
                        }
                    }
                    break;
            }
            current = current.getNext();
        }

        // Una vez encontrado el mejor PCB, lo REMOVEMOS de la cola usando su ID.
        if (bestPcb != null) {
            return readyQueue.removerPorId(bestPcb.getId());
        }
        return null;
    }
    
    /**
     * Reinserta un proceso en la Cola de Listos después de desalojo o E/S.
     */
    public void reinsertProcess(PCB pcb) {
        pcb.setStatus(ProcessStatus.READY); 
        
        // Simplemente se agrega al final. 
        // Para FCFS/RR es correcto. Para los demás, el próximo 'schedule' elegirá el mejor.
        readyQueue.agregar(pcb); 
    }


    // --- Getters y Setters para configuración dinámica ---
    
    public void setActiveAlgorithm(SchedulingAlgorithm algorithm) {
        this.activeAlgorithm = algorithm;
    }

    public SchedulingAlgorithm getActiveAlgorithm() {
        return activeAlgorithm;
    }
}