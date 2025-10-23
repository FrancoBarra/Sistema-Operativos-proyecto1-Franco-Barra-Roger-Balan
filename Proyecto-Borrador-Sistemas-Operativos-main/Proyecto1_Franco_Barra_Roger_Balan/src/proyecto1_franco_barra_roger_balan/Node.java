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

    // 1. Atributo para almacenar el objeto de datos (el PCB)
    private PCB pcb;

    // 2. Puntero al siguiente nodo en la lista
    private Node next;

    /**
     * Constructor: Inicializa un nuevo nodo con el PCB proporcionado.
     * @param pcb El objeto PCB que se almacenará en este nodo.
     */
    public Node(PCB pcb) {
        this.pcb = pcb;
        this.next = null; // Por defecto, el nuevo nodo no apunta a nada.
    }

    // --- Getters y Setters ---

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

/* NOTA: Recuerda que para que este código compile, debes tener la clase PCB ya definida.
   Si aún no la tienes, simplemente crea el archivo PCB.java como un stub por ahora. */
