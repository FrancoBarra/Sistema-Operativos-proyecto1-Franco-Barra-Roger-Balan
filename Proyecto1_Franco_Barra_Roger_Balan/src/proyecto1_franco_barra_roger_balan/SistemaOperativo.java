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
    
    // Todas las colas de proceso (instancias de la clase Cola personalizada)
    private final Cola readyQueue;
    private final Cola blockedQueue;
    private final Cola terminatedQueue;
    private final Cola suspendedReadyQueue; // Planificador a mediano plazo

    // --- Control de Hilos de E/S (Reemplazo manual de java.util.Map) ---
    private Thread[] ioThreads; // Arreglo simple de Java para rastrear hilos de E/S
    private int ioThreadCount; // Contador actual de hilos activos
    private static final int MAX_THREADS = 100; // Capacidad máxima del array

    // --- Control de Tiempo y Simulación ---
    private long globalClock;     // Contador de ciclos de reloj (ticks)
    private long cycleDuration;   // Duración en milisegundos de un ciclo (para Thread.sleep)
    private volatile boolean isRunning; // Control del bucle de simulación
    
    // --- Constructor ---
    public SistemaOperativo(int quantum, long initialCycleDuration) {
        // Inicialización de colas
        this.readyQueue = new Cola();
        this.blockedQueue = new Cola();
        this.terminatedQueue = new Cola();
        this.suspendedReadyQueue = new Cola();
        
        // Inicialización de componentes principales
        this.cpu = new CPU(quantum);
        // Inicializar Scheduler con un algoritmo por defecto
        this.scheduler = new Scheduler(readyQueue, Scheduler.SchedulingAlgorithm.FCFS, quantum); 
        
        // Inicialización del rastreo manual de hilos
        this.ioThreads = new Thread[MAX_THREADS];
        this.ioThreadCount = 0;
        
        this.globalClock = 0;
        this.cycleDuration = initialCycleDuration;
        this.isRunning = false;
    }
    
    /**
     * El método run() se ejecuta en un Thread separado y lanza el ciclo de reloj.
     */
    @Override
    public void run() {
        isRunning = true;
        
        while (isRunning) {
            
            tick();
            
            // Controlar la velocidad de la simulación
            try {
                // Thread.sleep es de java.lang.Thread (no util.*)
                java.lang.Thread.sleep(cycleDuration); 
            } catch (InterruptedException e) {
                java.lang.Thread.currentThread().interrupt();
                isRunning = false;
            }
        }
    }
    
    /**
     * El corazón del SO: Ejecuta todas las fases del ciclo de reloj.
     */
    public void tick() {
        if (!isRunning) return;
        
        globalClock++;
        
        // --- FASE 1: DESPACHO / INTERRUPCIÓN ---
        if (!cpu.isBusy() || scheduler.getActiveAlgorithm() == Scheduler.SchedulingAlgorithm.SJF_PREEMPTIVE) {
            dispatch();
        }
        
        // --- FASE 2: EJECUCIÓN DEL CICLO ---
        PCB pcbSaliente = cpu.executeCycle();
        
        // --- FASE 3: MANEJO DE DESALOJO O TERMINACIÓN ---
        if (pcbSaliente != null) {
            handleDesalojo(pcbSaliente);
        }

        // FASE 4: (Aquí iría la lógica del Planificador a Mediano Plazo para Suspender/Reanudar)
        
        // NOTIFICAR GUI para refrescar la vista
    }
    
    /**
     * Maneja el proceso de desalojo (interrupción, terminación, o E/S).
     */
    private void handleDesalojo(PCB pcb) {
        switch (pcb.getStatus()) {
            case TERMINATED:
                terminatedQueue.agregar(pcb);
                // Detener y eliminar rastreo del Thread de E/S asociado
                removeIOThread(pcb.getId());
                break;
                
            case BLOCKED:
                blockedQueue.agregar(pcb);
                // Lanzar el Thread de E/S para simular la espera
                Thread ioThread = new Thread(pcb, this);
                addIOThread(ioThread);
                ioThread.start();
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
            // Sacar de la cola si es FCFS o Round Robin (el Scheduler no lo hace directamente)
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
     * Recibe la notificación del Thread de E/S cuando la operación ha terminado.
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
            
            // 3. Eliminar el rastreo del Thread de E/S
            removeIOThread(pcbId); 
        }
    }
    
    // --- Lógica Manual de Arreglos (Reemplazo de Map/List) ---

    /** Agrega un Thread de E/S al array de rastreo. */
    private void addIOThread(Thread thread) {
        if (ioThreadCount < MAX_THREADS) {
            ioThreads[ioThreadCount++] = thread;
        }
    }
    
    /** Elimina un Thread de E/S del array de rastreo. */
    private void removeIOThread(int pcbId) {
        for (int i = 0; i < ioThreadCount; i++) {
            if (ioThreads[i] != null && ioThreads[i].getPcb().getId() == pcbId) {
                // Detener el Thread (si aún no lo está)
                ioThreads[i].stopRunning();
                // Desplazar el resto de elementos del array hacia atrás (simulando remoción de una lista)
                for (int j = i; j < ioThreadCount - 1; j++) {
                    ioThreads[j] = ioThreads[j+1];
                }
                ioThreads[ioThreadCount - 1] = null; // Limpiar la última posición
                ioThreadCount--;
                return;
            }
        }
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
    public CPU getCpu() { return cpu; }
    public long getGlobalClock() { return globalClock; }
    public Scheduler getScheduler() { return scheduler; }
    public boolean isRunning() { return isRunning; }
}