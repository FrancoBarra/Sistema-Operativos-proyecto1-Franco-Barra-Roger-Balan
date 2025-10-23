/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 * Clase Cola: Implementa una estructura de datos de Cola (Queue) 
 * basada en nodos para almacenar PCBs.
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
     * Agrega un PCB al final de la cola (FIFO).
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
     * Saca y retorna el PCB del frente de la cola (FIFO).
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
     * Retorna el PCB del frente sin removerlo.
     */
    public PCB verFrente() {
        if (isEmpty()) {
            return null;
        }
        return this.head.getPcb();
    }

    /**
     * Remueve un PCB específico de la cola por su ID.
     * Este método es usado por el Scheduler (para SJF/HRRN/Prioridad) 
     * y por el IOManager (para sacar procesos bloqueados).
     * @param pcbId El ID del proceso a remover.
     * @return El PCB removido, o null si no se encuentra.
     */
    public PCB removerPorId(int pcbId) {
        Node current = this.head;
        Node previous = null;

        while (current != null) {
            if (current.getPcb().getId() == pcbId) {
                
                // Caso 1: Remoción de la cabeza (head)
                if (previous == null) {
                    this.head = current.getNext();
                    if (this.head == null) { 
                        this.tail = null; // La cola queda vacía
                    }
                } 
                // Caso 2: Remoción de la cola (tail)
                else if (current == this.tail) {
                    previous.setNext(null);
                    this.tail = previous;
                }
                // Caso 3: Remoción de un nodo intermedio
                else {
                    previous.setNext(current.getNext());
                }

                this.size--;
                return current.getPcb();
            }
            previous = current;
            current = current.getNext();
        }
        return null; // PCB no encontrado
    }
    
    /**
     * Verifica si la cola está vacía.
     */
    public boolean isEmpty() {
        return this.head == null;
    }

    /**
     * Retorna el número de elementos en la cola.
     */
    public int getSize() {
        return size;
    }
    
    // Getter para la cabeza (usado por Scheduler para iterar)
    public Node getHead() {
        return head;
    }
}