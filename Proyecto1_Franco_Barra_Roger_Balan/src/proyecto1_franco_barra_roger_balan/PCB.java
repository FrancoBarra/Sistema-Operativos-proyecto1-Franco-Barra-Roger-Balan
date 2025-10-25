/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 * Clase PCB (Process Control Block): Contiene toda la información
 * necesaria para la gestión de un proceso por el Sistema Operativo.
 */
public class PCB {

    private static volatile int ID_COUNTER = 1000;

    // --- 1. Identificación y Estado ---
    private final int id;               
    private final String name;          
    private ProcessStatus status;       
    private final long arrivalTime;     
    
    // --- 2. Registros de Hardware Simulados ---
    private int programCounter;         
    private int memoryAddressRegister;  
    
    // --- 3. Ejecución y Control ---
    private final int longitudPrograma; 
    private int ciclosRestantes;        
    
    // --- 4. Planificación y Tipo de Proceso ---
    private final ProcessType type;     
    private int priority; // Añadido para planificación por Prioridad (Inicializado en constructor)
    
    // --- 5. Parámetros de Excepción (Solo para I/O_BOUND) ---
    private final int ciclosParaExcepcion;   // Cada cuántos ciclos se solicita I/O
    private int ciclosIOEspera;              // Ciclos restantes para terminar la I/O
    private final int ciclosParaSatisfacerIO; // Total de ciclos que tarda la I/O
    
    // --- 6. Métricas de Rendimiento ---
    private long tiempoFinalizacion = 0;        
    private long tiempoRespuesta = -1;           // Tiempo de la primera ejecución (First Run)
    private long tiempoEsperaAcumulado = 0;     // Tiempo total en la Ready Queue
    private long tiempoEnCPUAcumulado = 0;      // Tiempo total en la CPU
    private long tiempoEnBloqueadoAcumulado = 0; // Tiempo total en la Blocked Queue
    

    /**
     * Constructor principal del PCB.
     * @param name Nombre del proceso.
     * @param longitudPrograma Número total de ciclos requeridos.
     * @param type Tipo de proceso (CPU_BOUND/IO_BOUND).
     * @param ciclosParaExcepcion Ciclos para generar interrupción de E/S.
     * @param ciclosParaSatisfacerIO Ciclos que dura la E/S.
     * @param arrivalTime El ciclo de reloj en que el proceso entra (normalmente 0).
     */
    public PCB(String name, int longitudPrograma, ProcessType type, 
               int ciclosParaExcepcion, int ciclosParaSatisfacerIO, long arrivalTime) {
        
        this.id = ID_COUNTER++;
        this.name = name;
        this.status = ProcessStatus.NEW;
        this.arrivalTime = arrivalTime;

        this.longitudPrograma = longitudPrograma;
        this.ciclosRestantes = longitudPrograma;
        
        this.type = type;
        this.priority = 1; // Prioridad por defecto
        
        this.ciclosParaExcepcion = ciclosParaExcepcion;
        this.ciclosParaSatisfacerIO = ciclosParaSatisfacerIO;
        this.ciclosIOEspera = 0;
    }
    public PCB(PCB original) {
    // 1. Copiamos los valores de identificación e inmutables
    this.id = original.id;
    this.name = original.name;
    this.arrivalTime = original.arrivalTime;
    this.longitudPrograma = original.longitudPrograma;
    this.type = original.type;
    this.priority = original.priority;
    this.ciclosParaExcepcion = original.ciclosParaExcepcion;
    this.ciclosParaSatisfacerIO = original.ciclosParaSatisfacerIO;

    // 2. ¡RESETEAMOS los valores de estado y métricas!
    this.status = ProcessStatus.NEW; 
    this.programCounter = 0;
    this.memoryAddressRegister = 0;
    this.ciclosRestantes = this.longitudPrograma; // Reseteado
    this.ciclosIOEspera = 0;

    this.tiempoFinalizacion = 0;
    this.tiempoRespuesta = -1; // ¡Importante resetear a -1!
    this.tiempoEsperaAcumulado = 0;
    this.tiempoEnCPUAcumulado = 0;
    this.tiempoEnBloqueadoAcumulado = 0;
}
    
    // =========================================================================
    // --- SETTERS / MUTATORS ---
    // =========================================================================
    
    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
    
    public void setCiclosRestantes(int ciclosRestantes) {
        this.ciclosRestantes = ciclosRestantes;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public void setCiclosIOEspera(int ciclosIOEspera) {
        this.ciclosIOEspera = ciclosIOEspera;
    }
    
    public void setTiempoFinalizacion(long tiempoFinalizacion) {
        this.tiempoFinalizacion = tiempoFinalizacion;
    }
    
    /**
     * Registra el ciclo en que el proceso corre por primera vez.
     * @param cycle El ciclo global.
     */
    public void setTiempoRespuesta(long cycle) {
        if (this.tiempoRespuesta == -1) {
            this.tiempoRespuesta = cycle;
        }
    }
    
    // =========================================================================
    // --- INCREMENTADORES ---
    // =========================================================================
    
    public void incrementarProgramCounter() {
        this.programCounter++;
    }
    
    public void decrementarCiclosRestantes() {
        this.ciclosRestantes--;
    }
    
    public void incrementarTiempoEspera() { 
        this.tiempoEsperaAcumulado++; 
    }
    
    public void incrementarTiempoCPU() { 
        this.tiempoEnCPUAcumulado++; 
    }
    
    public void incrementarTiempoBloqueado() { 
        this.tiempoEnBloqueadoAcumulado++; 
    }
    
    // =========================================================================
    // --- GETTERS ---
    // =========================================================================
    
    public int getId() { return id; }
    public String getName() { return name; }
    public ProcessStatus getStatus() { return status; }
    public long getArrivalTime() { return arrivalTime; }
    public int getProgramCounter() { return programCounter; }
    public int getLongitudPrograma() { return longitudPrograma; }
    public int getCiclosRestantes() { return ciclosRestantes; }
    public ProcessType getType() { return type; }
    public int getPriority() { return priority; }
    public int getCiclosParaExcepcion() { return ciclosParaExcepcion; }
    public int getCiclosIOEspera() { return ciclosIOEspera; }
    public int getCiclosParaSatisfacerIO() { return ciclosParaSatisfacerIO; }
    
    // Getters de Métricas
    public long getTiempoFinalizacion() { return tiempoFinalizacion; }
    public long getTiempoRespuesta() { return tiempoRespuesta; }
    public long getTiempoEsperaAcumulado() { return tiempoEsperaAcumulado; }
    public long getTiempoEnCPUAcumulado() { return tiempoEnCPUAcumulado; }
    public long getTiempoEnBloqueadoAcumulado() { return tiempoEnBloqueadoAcumulado; }
    
    // Métricas calculadas
    public long getTurnaroundTime() { 
        // Tiempo de Retorno (Turnaround Time) = Finalización - Llegada
        return this.tiempoFinalizacion - this.arrivalTime; 
    }
    
    public double getWeightedTurnaroundTime() {
        // Tiempo de Retorno Ponderado = Turnaround Time / Longitud del Programa
        return (double)getTurnaroundTime() / this.longitudPrograma;
    }
    public void executeInstruction() {
    if (this.ciclosRestantes > 0) {
        this.programCounter++;
        this.memoryAddressRegister++;
        this.ciclosRestantes--;
    }
}

/**
 * Incrementa solo el Program Counter (para métricas)
 */

/**
 * Incrementa solo el Memory Address Register (para métricas)  
 */
public void incrementarMemoryAddressRegister() {
    this.memoryAddressRegister++;
}

/**
 * Decrementa los ciclos restantes (para métricas)
 */
}