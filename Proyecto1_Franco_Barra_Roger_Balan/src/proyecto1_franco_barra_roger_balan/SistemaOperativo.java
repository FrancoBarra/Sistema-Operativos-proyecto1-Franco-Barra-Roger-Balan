/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

import java.util.concurrent.Semaphore;
import proyecto1_franco_barra_roger_balan.Scheduler.SchedulingAlgorithm;

/**
 * Clase SistemaOperativo: Es el motor de la simulación. 
 * Implementa Runnable para el ciclo de reloj y utiliza un Semáforo para la exclusión mutua.
 */
public class SistemaOperativo implements Runnable {

    // Componentes del SO
    private final CPU cpu;
    private final Scheduler scheduler;
    private final GestorEstados gestorEstados;
    private final GestorDeMetricas gestorMetricas;
    private final IOManager ioManager; 

    // Colas de Procesos
    private final Cola readyQueue;
    private final Cola blockedQueue;
    
    // Configuración y Control
    private final SimulacionConfig config;
    private long globalClock = 0; 
    private volatile boolean isRunning = false;
    private long cycleDuration; 

    // Concurrencia: Semáforo para acceso exclusivo a la CPU/Colas
    private final Semaphore cpuMutex = new Semaphore(1);
    
    private java.lang.Thread soThread;

    /**
     * Constructor del Sistema Operativo.
     */
    public SistemaOperativo(SimulacionConfig config, int quantumSize) {
        this.config = config;
        this.cycleDuration = (config != null) ? config.getCycleDuration() : 100; 

        // 1. Inicializar Colas
        this.readyQueue = new Cola();
        this.blockedQueue = new Cola();
        Cola suspendedReadyQueue = new Cola(); 

        // 2. Inicializar Componentes
        this.cpu = new CPU(quantumSize); 
        this.scheduler = new Scheduler(this.readyQueue, SchedulingAlgorithm.FCFS, quantumSize); 
        this.gestorMetricas = new GestorDeMetricas();
        
        // Constructor de GestorEstados solo con 4 parámetros
        this.gestorEstados = new GestorEstados(this.readyQueue, this.blockedQueue, 
                                               suspendedReadyQueue, this.scheduler);
        
        // 3. Inicializar I/O Manager (Hilo de E/S)
        this.ioManager = new IOManager(this.blockedQueue, this.gestorEstados, this.cpuMutex);
        
        // 4. Inyectar Procesos Iniciales
        if (config != null) {
            injectInitialProcesses(config.getInitialProcesses());
        }
    }
    
    public void startSimulation() {
        if (isRunning) return;
        this.isRunning = true;
        
        this.soThread = new java.lang.Thread(this);
        this.soThread.start();
        
        new java.lang.Thread(ioManager).start();
    }

    private void injectInitialProcesses(PCB[] pcbs) {
        if (pcbs == null) return;
        for (PCB pcb : pcbs) {
            pcb.setStatus(ProcessStatus.READY);
            this.readyQueue.agregar(pcb); 
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                // Sección Crítica: Adquirir Mutex
                cpuMutex.acquire(); 
                
                advanceCycle();
                
            } catch (InterruptedException e) {
                java.lang.Thread.currentThread().interrupt();
                break;
            } finally {
                // Sección Crítica: Liberar Mutex
                cpuMutex.release(); 
            }

            try {
                java.lang.Thread.sleep(cycleDuration);
            } catch (InterruptedException e) {
                java.lang.Thread.currentThread().interrupt();
                break;
            }
        }
        
        ioManager.stopRunning();
        System.out.println("Simulación terminada en ciclo: " + globalClock);
    }
    
    private void advanceCycle() {
        this.globalClock++;
        
        PCB expelledPcb = null;
        boolean wasPreempted = false; 
        boolean cpuWasBusy = cpu.isBusy(); 
        
        // 1. Ejecución (Modo Usuario)
        if (cpuWasBusy) {
            expelledPcb = cpu.executeCycle();
            
            if (expelledPcb == null) {
                // Proceso ejecutó y continúa
                PCB current = cpu.getCurrentProcess();
                current.incrementarTiempoEnCPU();
                if (current.getTiempoRespuesta() == 0) {
                     current.setTiempoRespuesta(globalClock); 
                }
            }
        } 
        
        // 2. Gestión de Interrupciones/Desalojos (Modo Kernel)
        if (expelledPcb != null) {
            handleInterrupt(expelledPcb);
            wasPreempted = true;
        }
        
        // 3. Planificación (Modo Kernel)
        if (!cpu.isBusy()) {
            // CPU libre: Planificar y despachar
            PCB nextPcb = scheduler.schedule(globalClock);
            if (nextPcb != null) {
                cpu.dispatch(nextPcb); 
                gestorMetricas.incrementarCiclosKernel(); // Tiempo de Dispatch
            } else {
                // CPU Ociosa
                gestorMetricas.incrementarCiclosCPUInactiva(); 
            }
        } else if (cpuWasBusy && wasPreempted) {
             // Hubo desalojo (Kernel Time)
             gestorMetricas.incrementarCiclosKernel();
        } 

        // 4. Actualizar contadores de espera
        updateReadyQueueCounters();
        
        // 5. Condición de Parada
        if (readyQueue.isEmpty() && blockedQueue.isEmpty() && !cpu.isBusy() && config != null && gestorMetricas.getContadorTerminados() >= config.getInitialProcesses().length) {
            this.isRunning = false;
        }
    }
    
    private void handleInterrupt(PCB pcb) {
        ProcessStatus finalStatus = pcb.getStatus();
        
        switch (finalStatus) {
            case TERMINATED:
                pcb.setTiempoFinalizacion(globalClock);
                gestorMetricas.agregarPCBTerminado(pcb);
                break;
                
            case BLOCKED:
                this.blockedQueue.agregar(pcb);
                break;
                
            case RUNNING: 
                // Quantum/Preemption: el SO lo reinserta como READY.
                pcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(pcb);
                break;
                
            default:
                System.err.println("Error: Proceso " + pcb.getId() + " desalojado con estado inesperado: " + finalStatus);
                break;
        }
        
        gestorMetricas.incrementarCiclosKernel(); 
    }
    
    private void updateReadyQueueCounters() {
        Node current = readyQueue.getHead();
        while (current != null) {
            current.getPcb().incrementarTiempoEspera();
            current = current.getNext();
        }
    }

    // --- Getters y Setters para la GUI ---
    // (Se omiten por brevedad, asumiendo que ya los tienes correctos)
    
    public void stopRunning() {
        this.isRunning = false;
    }
}