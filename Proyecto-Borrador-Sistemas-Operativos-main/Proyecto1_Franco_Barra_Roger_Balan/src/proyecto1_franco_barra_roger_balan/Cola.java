/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
public class Cola {

    private Node head;
    private Node tail; 
    private int size; 
    
    public Cola() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    /**
     * Agrega un PCB al final de la cola
     */
    public void agregar(PCB pcb) {
        Node newNode = new Node(pcb); 

        if (isEmpty()) {
            this.head = newNode;
            this.tail = newNode;
        } else {
            this.tail.setNext(newNode);
            this.tail = newNode;
        }
        this.size++;
    }

    /**
     * Remueve y retorna el PCB al frente de la cola
     */
    public PCB sacar() {
        if (isEmpty()) {
            return null;
        }

        PCB pcb = this.head.getPcb();
        this.head = this.head.getNext();

        if (this.head == null) {
            this.tail = null;
        }
        
        this.size--;
        return pcb;
    }
    
    /**
     * Retorna el PCB al frente de la cola sin removerlo
     */
    public PCB verFrente() {
        if (isEmpty()) {
            return null;
        }
        return this.head.getPcb();
    }
    
    /**
     * NUEVO MÉTODO: Retorna el nodo cabeza de la cola
     * Necesario para iterar sobre la cola en el SistemaOperativo
     */
    public Node getHead() {
        return this.head;
    }
    
    /**
     * Remueve un PCB específico por su ID
     */
    public PCB removerPorId(int pcbId) {
        Node current = this.head;
        Node previous = null;

        while (current != null) {
            if (current.getPcb().getId() == pcbId) {
                if (previous == null) {
                    // El nodo a remover es la cabeza
                    this.head = current.getNext();
                    if (this.head == null) { 
                        this.tail = null;
                    }
                } else if (current == this.tail) {
                    // El nodo a remover es la cola
                    previous.setNext(null);
                    this.tail = previous;
                } else {
                    // El nodo a remover está en medio
                    previous.setNext(current.getNext());
                }

                this.size--;
                return current.getPcb();
            }
            previous = current;
            current = current.getNext();
        }
        return null; // No se encontró el PCB
    }
    
    /**
     * Verifica si la cola está vacía
     */
    public boolean isEmpty() {
        return this.head == null;
    }

    /**
     * Retorna el tamaño actual de la cola
     */
    public int getSize() {
        return size;
    }
    
    /**
     * NUEVO MÉTODO: Retorna una representación en String de la cola
     * Útil para debugging y para la GUI
     */
    public String toString() {
        if (isEmpty()) {
            return "Cola vacía";
        }
        
        StringBuilder sb = new StringBuilder();
        Node current = this.head;
        int count = 0;
        
        while (current != null) {
            PCB pcb = current.getPcb();
            sb.append("[").append(count).append("] ")
              .append(pcb.getName())
              .append(" (ID: ").append(pcb.getId()).append(")")
              .append(" - Ciclos restantes: ").append(pcb.getCiclosRestantes());
            
            if (current.getNext() != null) {
                sb.append(" -> ");
            }
            
            current = current.getNext();
            count++;
        }
        
        return sb.toString();
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }
    
}