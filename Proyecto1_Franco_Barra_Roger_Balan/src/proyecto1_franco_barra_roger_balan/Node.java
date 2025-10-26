/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
/**
 * Clase Node: La unidad fundamental para construir la CustomQueue.
 * Contiene el Bloque de Control de Proceso (PCB) y la referencia al siguiente nodo.
 */
public class Node {

    private PCB pcb;

    private Node next;

    public Node(PCB pcb) {
        this.pcb = pcb;
        this.next = null; 
    }

    public PCB getPcb() {
        return pcb;
    }

    public void setPcb(PCB pcb) {
        this.pcb = pcb;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}