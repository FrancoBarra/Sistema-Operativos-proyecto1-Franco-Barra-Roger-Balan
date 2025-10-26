/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 * Clase CPU: Simula el hardware del procesador. Es un recurso pasivo que
 * ejecuta el PCB actualmente despachado y rastrea el quantum de tiempo.
 */
public class CPU {

    private PCB currentProcess; 
    private int quantumCounter;  
    private final int quantumSize; 

    public CPU(int quantumSize) {
        this.currentProcess = null;
        this.quantumCounter = 0;
        this.quantumSize = quantumSize;
    }

    public void dispatch(PCB pcb) {
        this.currentProcess = pcb;
        this.currentProcess.setStatus(ProcessStatus.RUNNING);
        this.quantumCounter = 0; 
    }

    public PCB executeCycle() {
        if (currentProcess == null) {
            return null;
        }

        currentProcess.executeInstruction();
        this.quantumCounter++; 

        if (currentProcess.getCiclosRestantes() <= 0) {
            currentProcess.setStatus(ProcessStatus.TERMINATED);
            return endExecution();
        }

        if (currentProcess.getType() == ProcessType.IO_BOUND) {
             if (currentProcess.getProgramCounter() > 0 && 
                 currentProcess.getProgramCounter() % currentProcess.getCiclosParaExcepcion() == 0) {
                
                currentProcess.setCiclosIOEspera(currentProcess.getCiclosParaSatisfacerIO());
                currentProcess.setStatus(ProcessStatus.BLOCKED);
                return endExecution(); 
            }
        }

        if (this.quantumCounter >= this.quantumSize) {
             return endExecution(); 
        }

        return null; 
    }
    
    private PCB endExecution() {
        PCB expelledPcb = this.currentProcess;
        this.currentProcess = null; 
        this.quantumCounter = 0;
        return expelledPcb;
    }
    
    public PCB getCurrentProcess() {
        return currentProcess;
    }
    
    public boolean isBusy() {
        return currentProcess != null;
    }
    
    public int getQuantumCounter() {
        return quantumCounter;
    }
    
    public int getQuantumSize() {
        return quantumSize;
    }
}