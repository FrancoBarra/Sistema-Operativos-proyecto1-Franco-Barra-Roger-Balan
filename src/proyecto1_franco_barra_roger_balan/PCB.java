/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
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

    // --- 5. Parámetros de Excepción (Solo para I/O_BOUND) ---
    private final int ciclosParaExcepcion;   
    private int ciclosIOEspera;              
    private final int ciclosParaSatisfacerIO; 
    
    // --- 6. Métricas de Rendimiento ---
    private long tiempoRespuesta; 
    private long tiempoEsperaAcumulado; 
    
    /**
     * Método sincronizado para generar un ID único de forma segura.
     * Usar 'synchronized' previene que dos hilos generen el mismo ID.
     */
    private static int getNextID() {
        // Bloque de código sincronizado para garantizar la exclusión mutua
        // y la unicidad del ID, cumpliendo la funcionalidad de AtomicInteger
        // sin depender de ella.
        synchronized (PCB.class) { 
            return ID_COUNTER++;
        }
    }

    /**
     * Constructor: Crea un nuevo Bloque de Control de Proceso.
     */
    public PCB(String name, int longitudPrograma, ProcessType type, 
               int ciclosParaExcepcion, int ciclosParaSatisfacerIO, long tiempoLlegada) {
        
        // Asignamos el ID único generado de forma segura
        this.id = getNextID(); 
        
        this.name = name;
        this.longitudPrograma = longitudPrograma;
        this.ciclosRestantes = longitudPrograma;
        this.type = type;
        this.ciclosParaExcepcion = ciclosParaExcepcion;
        this.ciclosParaSatisfacerIO = ciclosParaSatisfacerIO;
        this.status = ProcessStatus.NEW; 
        this.programCounter = 0;
        this.memoryAddressRegister = 0; 
        this.ciclosIOEspera = 0;
        this.arrivalTime = tiempoLlegada;
        this.tiempoRespuesta = -1;
        this.tiempoEsperaAcumulado = 0;
    }

    // --- Métodos de Ayuda ---
    
    public void executeInstruction() {
        if (this.ciclosRestantes > 0) {
            this.programCounter++;
            this.memoryAddressRegister++;
            this.ciclosRestantes--;
        }
    }

    // --- Getters y Setters ---

    // ... (Mantener todos los getters y setters del PCB anterior) ...

    public int getId() { return id; }
    public String getName() { return name; }
    public ProcessStatus getStatus() { return status; }
    public void setStatus(ProcessStatus status) { this.status = status; }
    //... (y así sucesivamente con todos los demás)
}