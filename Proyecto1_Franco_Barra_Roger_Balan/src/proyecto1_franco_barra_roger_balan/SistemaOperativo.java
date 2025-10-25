/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: SistemaOperativo.java

/**
 * Clase SistemaOperativo: El Kernel simulado. Controla el ciclo de reloj, 
 * los estados, la planificación y el despacho de procesos, evitando el uso
 * de cualquier clase de java.util.
 */
public class SistemaOperativo implements Runnable {
    
    // --- Atributos de Componentes ---
    private final CPU cpu;
    private final Scheduler scheduler;
    private final GestorEstados gestorEstados;
    private final ConfiguracionManager configManager;
    private final GestorDeMetricas gestorMetricas;
    private GUI guiListener;
    
    // Todas las colas de proceso (instancias de la clase Cola personalizada)
    private final Cola readyQueue;
    private final Cola blockedQueue;
    private final Cola terminatedQueue;
    private final Cola suspendedReadyQueue;

    // --- Control de Hilos de E/S (Usando IOThread en lugar de Thread) ---
    private IOThread[] ioThreads; // Cambiado de Thread[] a IOThread[]
    private int ioThreadCount;
    private static final int MAX_THREADS = 100;
    
    // --- Control de Tiempo y Simulación ---
    private long globalClock;
    private long cycleDuration;
    private volatile boolean isRunning;
    private boolean initialLoadCompleted = false;

    /**
     * CONSTRUCTOR PRINCIPAL PARA LA GUI.
     */
    public SistemaOperativo(CPU cpu, Scheduler scheduler, GestorEstados gestorEstados,
                            Cola readyQueue, Cola blockedQueue, ConfiguracionManager configManager) {
        
        // Asignación de componentes
        this.cpu = cpu;
        this.scheduler = scheduler;
        this.gestorEstados = gestorEstados;
        this.configManager = configManager;
        this.gestorMetricas = new GestorDeMetricas();
        
        // Asignación de colas
        this.readyQueue = readyQueue;
        this.blockedQueue = blockedQueue;
        this.terminatedQueue = new Cola(); 
        this.suspendedReadyQueue = gestorEstados.getSuspendedReadyQueue();

        // Inicialización del rastreo manual de hilos (ahora con IOThread)
        this.ioThreads = new IOThread[MAX_THREADS]; // Cambiado a IOThread[]
        this.ioThreadCount = 0;
        
        // Valores por defecto
        this.globalClock = 0;
        this.cycleDuration = 100;
        this.isRunning = false;
        
    }
    
    /**
     * Carga los procesos iniciales desde el ConfiguracionManager.
     */
    public void loadInitialProcesses() throws Exception {
        if (initialLoadCompleted) return;
        
        SimulacionConfig config = configManager.cargarConfiguracion();
        this.cycleDuration = config.getCycleDuration();
        
        // Cargar procesos en la cola de listos (NEW -> READY)
        PCB[] initialPCBs = config.getInitialProcesses();
        for (int i = 0; i < initialPCBs.length; i++) {
            PCB pcb = initialPCBs[i];
            if (pcb != null) {
                pcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(pcb);
            }
        }
        initialLoadCompleted = true;
    }
    
    // =================================================================
    // --- LÓGICA DEL CICLO DE RELOJ (Llamado por el Timer de la GUI) ---
    // =================================================================
    
    /**
     * Método llamado por el Timer de la GUI. Ejecuta un solo tick de la simulación.
     */
    public void avanzarCiclo() {
        if (isSimulationFinished()) {
            isRunning = false;
            return;
        }

        globalClock++;
        boolean cpuWasBusy = cpu.isBusy();
        
        // --- 0. Contadores de Tiempo Acumulado ---
        updateProcessTimeMetrics();
        
        // --- FASE 1: DESPACHO / INTERRUPCIÓN (Preemption o CPU Libre) ---
        if (!cpu.isBusy() || scheduler.isPreemptive()) {
              dispatch();
        }
        
        // --- FASE 2: EJECUCIÓN DEL CICLO ---
        PCB pcbSaliente = cpu.executeCycle();
        
        // --- FASE 3: MANEJO DE DESALOJO O TERMINACIÓN ---
        if (pcbSaliente != null) {
            handleDesalojo(pcbSaliente);
        }
        
        // --- FASE 4: Actualizar Métricas Globales ---
        boolean isCPUIdle = !cpu.isBusy();
        // CORREGIDO: usar el nombre correcto del método
        gestorMetricas.actualizarMetricasGlobales(globalClock, isCPUIdle, false);
    }
    
    /**
     * Actualiza los contadores de tiempo de espera y bloqueado en cada tick.
     */
    private void updateProcessTimeMetrics() {
        // Incrementar tiempo en READY
        Node currentReady = readyQueue.getHead();
        while (currentReady != null) {
            currentReady.getPcb().incrementarTiempoEspera();
            currentReady = currentReady.getNext();
        }

        // Incrementar tiempo en BLOCKED
        Node currentBlocked = blockedQueue.getHead();
        while (currentBlocked != null) {
            currentBlocked.getPcb().incrementarTiempoBloqueado();
            currentBlocked = currentBlocked.getNext();
        }

        // Incrementar tiempo en RUNNING
        if (cpu.getCurrentProcess() != null) {
            cpu.getCurrentProcess().incrementarTiempoCPU();
        }
    }
    
    // =================================================================
    // --- MÉTODOS EXISTENTES (Modificados para consistencia) ---
    // =================================================================

    /**
     * El método run() se ejecuta en un Thread separado y lanza el ciclo de reloj.
     */
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
    
    /**
     * Comprueba si todos los procesos han terminado.
     */
    public boolean isSimulationFinished() {
        return !cpu.isBusy() && readyQueue.isEmpty() && blockedQueue.isEmpty() && suspendedReadyQueue.isEmpty() && ioThreadCount == 0;
    }
    
    // --- Métodos Públicos para la GUI ---
    
    public void setCycleDuration(long duration) {
        this.cycleDuration = duration;
    }
    
    public void stopSimulation() {
        this.isRunning = false;
        // Detener todos los hilos de E/S
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
    
    // =================================================================
    // --- MÉTODOS PRIVADOS PARA GESTIÓN DE HILOS Y PROCESOS ---
    // =================================================================

    /** Agrega un IOThread al array de rastreo. */
    private void addIOThread(IOThread thread) { // Cambiado a IOThread
        if (ioThreadCount < MAX_THREADS) {
            ioThreads[ioThreadCount++] = thread;
        }
    }
    
    /** Elimina un IOThread del array de rastreo. */
    private void removeIOThread(int pcbId) {
        for (int i = 0; i < ioThreadCount; i++) {
            if (ioThreads[i] != null && ioThreads[i].getPcb().getId() == pcbId) {
                // Detener el IOThread
                ioThreads[i].stopRunning();
                // Desplazar el resto de elementos del array hacia atrás
                for (int j = i; j < ioThreadCount - 1; j++) {
                    ioThreads[j] = ioThreads[j+1];
                }
                ioThreads[ioThreadCount - 1] = null;
                ioThreadCount--;
                return;
            }
        }
    }

    /**
     * Maneja el proceso de desalojo (interrupción, terminación, o E/S).
     */
    private void handleDesalojo(PCB pcb) {
        switch (pcb.getStatus()) {
            case TERMINATED:
                pcb.setTiempoFinalizacion(globalClock);
                terminatedQueue.agregar(pcb);
                // CORREGIDO: usar el nombre correcto del método
                gestorMetricas.agregarPCBTerminado(pcb);
                removeIOThread(pcb.getId());
                break;
                
            case BLOCKED:
                blockedQueue.agregar(pcb);
                // CORREGIDO: crear IOThread en lugar de Thread
                IOThread ioThread = new IOThread(pcb, this); // Cambiado a IOThread
                addIOThread(ioThread);
                // Crear un java.lang.Thread para ejecutar el IOThread
                Thread thread = new Thread(ioThread);
                thread.start();
                break;
                
            case RUNNING: 
                // Esto ocurre si el quantum ha expirado (Round Robin)
                pcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(pcb);
                break;
                
            default:
                // El proceso desalojado vuelve a listo (por preemption o por defecto)
                pcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(pcb);
                break;
        }
    }
    
    /**
     * Invoca al Scheduler para seleccionar el próximo proceso para la CPU.
     */
    private void dispatch() {
        PCB nextPcb = scheduler.selectNextProcess();
        
        if (nextPcb != null) {
            // Sacar de la cola si es FCFS o Round Robin
            if (scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.FCFS || 
                scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.ROUND_ROBIN) {
                readyQueue.sacar();
            } else if (scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.SJF_PREEMPTIVE) {
                // Para SJF Apropiativo, el selectNextProcess() ya removió el PCB más corto.
            }
            
            // Verificar si hay un proceso en ejecución (para Preemption)
            PCB currentPcb = cpu.getCurrentProcess();
            if (currentPcb != null && currentPcb.getStatus() == ProcessStatus.RUNNING) {
                // Context Switching: Guardar el proceso actual y devolverlo a la cola
                currentPcb.setStatus(ProcessStatus.READY);
                scheduler.reinsertProcess(currentPcb);
            }

            // Despacho final a la CPU
            cpu.dispatch(nextPcb);
            
            // Registrar métricas
            if (nextPcb.getTiempoRespuesta() == -1) {
                
                nextPcb.setTiempoRespuesta(globalClock);
            }

        }
    }
    
    /**
     * Recibe la notificación del IOThread cuando la operación ha terminado.
     * Mueve el PCB de BLOCKED a READY. Debe ser synchronized para seguridad concurrente.
     * @param pcbId El ID del proceso cuya E/S terminó.
     */
    public synchronized void processIOCompleted(int pcbId) {
        // 1. Remover el PCB de la cola de Bloqueados
        PCB pcb = blockedQueue.removerPorId(pcbId);
        
        if (pcb != null) {
            // 2. Mover a estado Listo y reinsertar
            pcb.setStatus(ProcessStatus.READY);
            scheduler.reinsertProcess(pcb);
            
            // 3. Eliminar el rastreo del IOThread
            removeIOThread(pcbId);
        }
    }
    public void setGUIListener(GUI listener) {
    this.guiListener = listener;
}
}