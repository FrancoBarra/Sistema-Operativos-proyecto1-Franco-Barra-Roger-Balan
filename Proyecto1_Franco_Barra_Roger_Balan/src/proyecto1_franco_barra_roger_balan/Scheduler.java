/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;


public class Scheduler {


    public static enum SchedulingAlgorithm {
        FCFS,          
        SJF_NON_PREEMPTIVE, 
        SJF_PREEMPTIVE,    
        ROUND_ROBIN,   
        PRIORITY_NON_PREEMPTIVE, 
        PRIORITY_PREEMPTIVE      
    }

    private Cola readyQueue;
    private SchedulingAlgorithm activeAlgorithm;
    private int timeQuantum;


    public Scheduler(Cola readyQueue, SchedulingAlgorithm initialAlgorithm, int quantum) {
        this.readyQueue = readyQueue;
        this.activeAlgorithm = initialAlgorithm;
        this.timeQuantum = quantum;
    }

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
    

    
    private PCB _selectFCFS() {
        return readyQueue.verFrente(); 
    }
    
    private PCB _selectSJF() {
        if (readyQueue.isEmpty()) return null;
        
        Node current = readyQueue.getHead();
        PCB shortestJob = current.getPcb();
        Node shortestNode = current;
        

        while (current != null) {
            PCB currentPCB = current.getPcb();
            if (currentPCB.getCiclosRestantes() < shortestJob.getCiclosRestantes()) {
                shortestJob = currentPCB;
                shortestNode = current;
            }
            current = current.getNext();
        }
        

        if (shortestNode == readyQueue.getHead()) {

            return readyQueue.sacar();
        } else {
 
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
        

        while (current != null) {
            PCB currentPCB = current.getPcb();
            if (currentPCB.getPriority() < highestPriority.getPriority()) {
                highestPriority = currentPCB;
                highestNode = current;
            }
            current = current.getNext();
        }
        

        if (highestNode == readyQueue.getHead()) {
            return readyQueue.sacar();
        } else {
            return readyQueue.removerPorId(highestPriority.getId());
        }
    }
    

    public void reinsertProcess(PCB pcb) {

        pcb.setStatus(ProcessStatus.READY); 
        
        if (activeAlgorithm == SchedulingAlgorithm.ROUND_ROBIN) {
 
            readyQueue.agregar(pcb);
        } else if (activeAlgorithm == SchedulingAlgorithm.SJF_NON_PREEMPTIVE || 
                   activeAlgorithm == SchedulingAlgorithm.SJF_PREEMPTIVE) {

            _insertOrderedByCycles(pcb);
        } else if (activeAlgorithm == SchedulingAlgorithm.PRIORITY_NON_PREEMPTIVE || 
                   activeAlgorithm == SchedulingAlgorithm.PRIORITY_PREEMPTIVE) {
       
            _insertOrderedByPriority(pcb);
        } else {

            readyQueue.agregar(pcb);
        }
    }
    

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
          
            newNode.setNext(readyQueue.getHead());

            readyQueue.agregar(pcb);
        } else {

            newNode.setNext(current);
            previous.setNext(newNode);
            if (current == null) {

                readyQueue.agregar(pcb);
            }
        }
    }
    

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

            newNode.setNext(readyQueue.getHead());
  
            readyQueue.agregar(pcb);
        } else {

            newNode.setNext(current);
            previous.setNext(newNode);
            if (current == null) {

                readyQueue.agregar(pcb);
            }
        }
    }


    
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
    

    public int getReadyQueueSize() {
        return readyQueue.getSize();
    }
}