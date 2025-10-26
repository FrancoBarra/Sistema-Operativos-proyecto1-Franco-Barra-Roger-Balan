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

    private final int id;               
    private final String name;          
    private ProcessStatus status;       
    private final long arrivalTime;     
    
    private int programCounter;         
    private int memoryAddressRegister;  
    
    private final int longitudPrograma; 
    private int ciclosRestantes;        
    
    private final ProcessType type;     
    private int priority; 
    
    private final int ciclosParaExcepcion;   
    private int ciclosIOEspera;              
    private final int ciclosParaSatisfacerIO; 
    
    private long tiempoFinalizacion = 0;        
    private long tiempoRespuesta = -1;           
    private long tiempoEsperaAcumulado = 0;     
    private long tiempoEnCPUAcumulado = 0;      
    private long tiempoEnBloqueadoAcumulado = 0; 
    

    public PCB(String name, int longitudPrograma, ProcessType type, 
               int ciclosParaExcepcion, int ciclosParaSatisfacerIO, long arrivalTime) {
        
        this.id = ID_COUNTER++;
        this.name = name;
        this.status = ProcessStatus.NEW;
        this.arrivalTime = arrivalTime;

        this.longitudPrograma = longitudPrograma;
        this.ciclosRestantes = longitudPrograma;
        
        this.type = type;
        this.priority = 1; 
        
        this.ciclosParaExcepcion = ciclosParaExcepcion;
        this.ciclosParaSatisfacerIO = ciclosParaSatisfacerIO;
        this.ciclosIOEspera = 0;
    }
    public PCB(PCB original) {
    this.id = original.id;
    this.name = original.name;
    this.arrivalTime = original.arrivalTime;
    this.longitudPrograma = original.longitudPrograma;
    this.type = original.type;
    this.priority = original.priority;
    this.ciclosParaExcepcion = original.ciclosParaExcepcion;
    this.ciclosParaSatisfacerIO = original.ciclosParaSatisfacerIO;

    this.status = ProcessStatus.NEW;
    this.programCounter = 0;
    this.memoryAddressRegister = 0;
    this.ciclosRestantes = this.longitudPrograma; 
    this.ciclosIOEspera = 0;
    
    this.tiempoFinalizacion = 0;
    this.tiempoRespuesta = -1;           
    this.tiempoEsperaAcumulado = 0;
    this.tiempoEnCPUAcumulado = 0;
    this.tiempoEnBloqueadoAcumulado = 0;
}

    
    public void setStatus(ProcessStatus status) {
        this.status = status;
    }
    
    public void setTiempoFinalizacion(long tiempoFinalizacion) {
        this.tiempoFinalizacion = tiempoFinalizacion;
    }
    
    public void setTiempoRespuesta(long tiempoRespuesta) {
        if (this.tiempoRespuesta == -1) {
            this.tiempoRespuesta = tiempoRespuesta;
        }
    }
    
    public void setCiclosIOEspera(int ciclosIOEspera) {
        this.ciclosIOEspera = ciclosIOEspera;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
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
    public int getMemoryAddressRegister() {
    return this.memoryAddressRegister;
}
    
    public long getTiempoFinalizacion() { return tiempoFinalizacion; }
    public long getTiempoRespuesta() { return tiempoRespuesta; }
    public long getTiempoEsperaAcumulado() { return tiempoEsperaAcumulado; }
    public long getTiempoEnCPUAcumulado() { return tiempoEnCPUAcumulado; }
    public long getTiempoEnBloqueadoAcumulado() { return tiempoEnBloqueadoAcumulado; }
    
    public long getTurnaroundTime() { 
        return this.tiempoFinalizacion - this.arrivalTime; 
    }
    
    public double getWeightedTurnaroundTime() {
        return (double)getTurnaroundTime() / this.longitudPrograma;
    }
    public void executeInstruction() {
    if (this.ciclosRestantes > 0) {
        this.programCounter++;
        this.memoryAddressRegister++;
        this.ciclosRestantes--;
    }
}

}