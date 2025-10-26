/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;


public class SistemaOperativo implements Runnable {
    

    private final CPU cpu;
    private final Scheduler scheduler;
    private final GestorEstados gestorEstados;
    private final GestorDeMetricas gestorMetricas;
    private GUI guiListener;
    

    private final Cola readyQueue;
    private final Cola blockedQueue;
    private final Cola terminatedQueue;
    private final Cola suspendedReadyQueue;

  
    private IOThread[] ioThreads; 
    private int ioThreadCount;
    private static final int MAX_THREADS = 100;
    

    private long globalClock;
    private long cycleDuration;
    private volatile boolean isRunning;
    private boolean initialLoadCompleted = false;

  
    public SistemaOperativo(CPU cpu, Scheduler scheduler, GestorEstados gestorEstados,
                              Cola readyQueue, Cola blockedQueue) {
        
   
        this.cpu = cpu;
        this.scheduler = scheduler;
        this.gestorEstados = gestorEstados;
      
        this.gestorMetricas = new GestorDeMetricas();
        
    
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.terminatedQueue = new Cola(); 
        this.suspendedReadyQueue = gestorEstados.getSuspendedReadyQueue();


        this.ioThreads = new IOThread[MAX_THREADS]; 
        this.ioThreadCount = 0;
        

        this.globalClock = 0;
        this.cycleDuration = 100; 
        this.isRunning = false;
        this.initialLoadCompleted = true; 
    }    
 
    public void avanzarCiclo() {
        if (isSimulationFinished()) {
            isRunning = false;
            return;
        }

        globalClock++;
        boolean cpuWasBusy = cpu.isBusy();
        
 
        updateProcessTimeMetrics();
        
  
        if (!cpu.isBusy() || scheduler.isPreemptive()) {
              dispatch();
        }
        

        PCB pcbSaliente = cpu.executeCycle();
        
  
        if (pcbSaliente != null) {
            handleDesalojo(pcbSaliente);
        }
        
   
        boolean isCPUIdle = !cpu.isBusy();
       
        gestorMetricas.actualizarMetricasGlobales(globalClock, isCPUIdle, false);
    }
    

    private void updateProcessTimeMetrics() {
   
        Node currentReady = readyQueue.getHead();
        while (currentReady != null) {
            currentReady.getPcb().incrementarTiempoEspera();
            currentReady = currentReady.getNext();
        }

   
        Node currentBlocked = blockedQueue.getHead();
        while (currentBlocked != null) {
            currentBlocked.getPcb().incrementarTiempoBloqueado();
            currentBlocked = currentBlocked.getNext();
        }

     
        if (cpu.getCurrentProcess() != null) {
            cpu.getCurrentProcess().incrementarTiempoCPU();
        }
    }
    
   
    @Override
    public void run() {
        isRunning = true;
        
        while (isRunning) {
            try {
                java.lang.Thread.sleep(cycleDuration); 
            } catch (InterruptedException e) {
                java.lang.Thread.currentThread().interrupt();
                isRunning = false;
            }
        }
    }
    
 
    public boolean isSimulationFinished() {
        return !cpu.isBusy() && readyQueue.isEmpty() && blockedQueue.isEmpty() && suspendedReadyQueue.isEmpty() && ioThreadCount == 0;
    }
    

    
    public void setCycleDuration(long duration) {
        this.cycleDuration = duration;
    }
    
    public void stopSimulation() {
        this.isRunning = false;
   
        for (int i = 0; i < ioThreadCount; i++) {
            if (ioThreads[i] != null) {
                 ioThreads[i].stopRunning();
            }
        }
    }
    
    public Cola getReadyQueue() { return readyQueue; }
    public Cola getBlockedQueue() { return blockedQueue; }
    public Cola getTerminatedQueue() { return terminatedQueue; }
    public CPU getCpu() { return cpu; }
    public long getGlobalClock() { return globalClock; }
    public Scheduler getScheduler() { return scheduler; }
    public boolean isRunning() { return isRunning; }
    
    public GestorDeMetricas getGestorMetricas() {
        return gestorMetricas; 
    }
    

  
    private void addIOThread(IOThread thread) {
        if (ioThreadCount < MAX_THREADS) {
            ioThreads[ioThreadCount++] = thread;
        }
    }
    
   
    private void removeIOThread(int pcbId) {
        for (int i = 0; i < ioThreadCount; i++) {
            if (ioThreads[i] != null && ioThreads[i].getPcb().getId() == pcbId) {
               
                ioThreads[i].stopRunning();
               
                for (int j = i; j < ioThreadCount - 1; j++) {
                    ioThreads[j] = ioThreads[j+1];
                }
                ioThreads[ioThreadCount - 1] = null;
                ioThreadCount--;
                return;
            }
        }
    }

   
    private void handleDesalojo(PCB pcb) {
        switch (pcb.getStatus()) {
            case TERMINATED:
                pcb.setTiempoFinalizacion(globalClock);
                terminatedQueue.agregar(pcb);
              
                gestorMetricas.agregarPCBTerminado(pcb);
                removeIOThread(pcb.getId());
                break;
                
            case BLOCKED:
                blockedQueue.agregar(pcb);
        
                IOThread ioThread = new IOThread(pcb, this); 
                addIOThread(ioThread);
              
                Thread thread = new Thread(ioThread);
                thread.start();
                break;
                
            case RUNNING: 
              
                pcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(pcb);
                break;
                
            default:
            
                pcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(pcb);
                break;
        }
    }
    

    private void dispatch() {
        PCB nextPcb = scheduler.selectNextProcess();
        
        if (nextPcb != null) {
    
            if (scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.FCFS || 
                scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.ROUND_ROBIN) {
                readyQueue.sacar();
            } else if (scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.SJF_PREEMPTIVE) {
             
            }
            
       
            PCB currentPcb = cpu.getCurrentProcess();
            if (currentPcb != null && currentPcb.getStatus() == ProcessStatus.RUNNING) {
     
                currentPcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(currentPcb);
            }

          
            cpu.dispatch(nextPcb);
            
     
            if (nextPcb.getTiempoRespuesta() == -1) {
                
                nextPcb.setTiempoRespuesta(globalClock);
            }

        }
    }
    
 
    public synchronized void processIOCompleted(int pcbId) {

        PCB pcb = blockedQueue.removerPorId(pcbId);
        
        if (pcb != null) {
  
            pcb.setStatus(ProcessStatus.READY);
            scheduler.reinsertProcess(pcb);
            
          
            removeIOThread(pcbId);
        }
    }
    public void setGUIListener(GUI listener) {
    this.guiListener = listener;
}
}