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
     * Enumeración SchedulingAlgorithm: Define las 6 políticas de planificación requeridas.
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
    private int timeQuantum;

    /**
     * Constructor
     */
    public Scheduler(Cola readyQueue, SchedulingAlgorithm initialAlgorithm, int quantum) {
        this.readyQueue = readyQueue;
        this.activeAlgorithm = initialAlgorithm;
        this.timeQuantum = quantum;
    }

    /**
     * NUEVO MÉTODO: Determina si el algoritmo actual es apropiativo
     * @return true si el algoritmo es apropiativo, false en caso contrario
     */
    public boolean isPreemptive() {
        switch (activeAlgorithm) {
            case SJF_PREEMPTIVE:
            case PRIORITY_PREEMPTIVE:
            case ROUND_ROBIN:
                return true;
            case FCFS:
            case SJF_NON_PREEMPTIVE:
            case PRIORITY_NON_PREEMPTIVE:
            default:
                return false;
        }
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

        switch (activeAlgorithm) {
            case FCFS:
                return _selectFCFS();
            case SJF_NON_PREEMPTIVE:
            case SJF_PREEMPTIVE:
                return _selectSJF(); 
            case ROUND_ROBIN:
                return _selectRoundRobin();
            case PRIORITY_NON_PREEMPTIVE:
            case PRIORITY_PREEMPTIVE:
                return _selectPriority();
            default:
                return _selectFCFS(); 
        }
    }
    
    // --- Métodos de Implementación de Algoritmos ---
    
    private PCB _selectFCFS() {
        return readyQueue.verFrente(); 
    }
    
    private PCB _selectSJF() {
        if (readyQueue.isEmpty()) return null;
        
        Node current = readyQueue.getHead();
        PCB shortestJob = current.getPcb();
        Node shortestNode = current;
        
        // Buscar el proceso con menor cantidad de ciclos restantes
        while (current != null) {
            PCB currentPCB = current.getPcb();
            if (currentPCB.getCiclosRestantes() < shortestJob.getCiclosRestantes()) {
                shortestJob = currentPCB;
                shortestNode = current;
            }
            current = current.getNext();
        }
        
        // Remover el proceso seleccionado de la cola
        if (shortestNode == readyQueue.getHead()) {
            // Si es el primero, usar sacar() para eficiencia
            return readyQueue.sacar();
        } else {
            // Si no es el primero, remover por ID
            return readyQueue.removerPorId(shortestJob.getId());
        }
    }
    
    private PCB _selectRoundRobin() {
        return readyQueue.verFrente();
    }
    
    private PCB _selectPriority() {
        if (readyQueue.isEmpty()) return null;
        
        Node current = readyQueue.getHead();
        PCB highestPriority = current.getPcb();
        Node highestNode = current;
        
        // Buscar el proceso con mayor prioridad (número más bajo = mayor prioridad)
        while (current != null) {
            PCB currentPCB = current.getPcb();
            if (currentPCB.getPriority() < highestPriority.getPriority()) {
                highestPriority = currentPCB;
                highestNode = current;
            }
            current = current.getNext();
        }
        
        // Remover el proceso seleccionado de la cola
        if (highestNode == readyQueue.getHead()) {
            return readyQueue.sacar();
        } else {
            return readyQueue.removerPorId(highestPriority.getId());
        }
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
        } else if (activeAlgorithm == SchedulingAlgorithm.SJF_NON_PREEMPTIVE || 
                   activeAlgorithm == SchedulingAlgorithm.SJF_PREEMPTIVE) {
            // Para SJF, insertar manteniendo orden por ciclos restantes
            _insertOrderedByCycles(pcb);
        } else if (activeAlgorithm == SchedulingAlgorithm.PRIORITY_NON_PREEMPTIVE || 
                   activeAlgorithm == SchedulingAlgorithm.PRIORITY_PREEMPTIVE) {
            // Para Prioridad, insertar manteniendo orden por prioridad
            _insertOrderedByPriority(pcb);
        } else {
            // FCFS y por defecto: insertar al final
            readyQueue.agregar(pcb);
        }
    }
    
    /**
     * Inserta un PCB en la cola ordenado por ciclos restantes (para SJF)
     */
    private void _insertOrderedByCycles(PCB pcb) {
        if (readyQueue.isEmpty()) {
            readyQueue.agregar(pcb);
            return;
        }
        
        Node newNode = new Node(pcb);
        Node current = readyQueue.getHead();
        Node previous = null;
        
        while (current != null && current.getPcb().getCiclosRestantes() <= pcb.getCiclosRestantes()) {
            previous = current;
            current = current.getNext();
        }
        
        if (previous == null) {
            // Insertar al inicio
            newNode.setNext(readyQueue.getHead());
            // Necesitaríamos acceso directo a head aquí - por simplicidad, agregamos al final
            readyQueue.agregar(pcb);
        } else {
            // Insertar en medio
            newNode.setNext(current);
            previous.setNext(newNode);
            if (current == null) {
                // Actualizar tail si se inserta al final
                // Necesitaríamos acceso a tail - por simplicidad, usar agregar normal
                readyQueue.agregar(pcb);
            }
        }
    }
    
    /**
     * Inserta un PCB en la cola ordenado por prioridad (para Priority)
     */
    private void _insertOrderedByPriority(PCB pcb) {
        if (readyQueue.isEmpty()) {
            readyQueue.agregar(pcb);
            return;
        }
        
        Node newNode = new Node(pcb);
        Node current = readyQueue.getHead();
        Node previous = null;
        
        while (current != null && current.getPcb().getPriority() <= pcb.getPriority()) {
            previous = current;
            current = current.getNext();
        }
        
        if (previous == null) {
            // Insertar al inicio
            newNode.setNext(readyQueue.getHead());
            // Por simplicidad, usar agregar normal
            readyQueue.agregar(pcb);
        } else {
            // Insertar en medio
            newNode.setNext(current);
            previous.setNext(newNode);
            if (current == null) {
                // Actualizar tail si se inserta al final
                readyQueue.agregar(pcb);
            }
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
    
    /**
     * NUEVO MÉTODO: Retorna el tamaño actual de la cola de listos
     */
    public int getReadyQueueSize() {
        return readyQueue.getSize();
    }
}