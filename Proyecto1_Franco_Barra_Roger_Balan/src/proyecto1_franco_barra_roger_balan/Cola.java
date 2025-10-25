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
    @Override
public String toString() {
    if (isEmpty()) {
        // Un HTML simple para "Cola vacía"
        return "<html><body style='font-family: Segoe UI; font-size: 10pt; color: #888888;'><i>Cola vacía</i></body></html>";
    }

    // Usamos StringBuilder para construir el HTML
    StringBuilder sb = new StringBuilder();

    // Estilos CSS para que se vea como en la imagen
    sb.append("<html><body style='font-family: Segoe UI; font-size: 10pt;'>");

    Node current = this.head;

    while (current != null) {
        PCB pcb = current.getPcb();

        // Definimos colores según el estado (¡puedes cambiarlos!)
        String estadoColor = "#000000"; // Negro por defecto
        if (pcb.getStatus() == ProcessStatus.READY) {
            estadoColor = "#008800"; // Verde
        } else if (pcb.getStatus() == ProcessStatus.BLOCKED) {
            estadoColor = "#DD8800"; // Naranja
        } else if (pcb.getStatus() == ProcessStatus.TERMINATED) {
            estadoColor = "#CC0000"; // Rojo
        }

        // Construimos la línea para este PCB
        sb.append("<p style='margin: 0; padding: 2px;'>"); // Párrafo sin margen
        sb.append("<b>").append(pcb.getName()).append("</b>"); // Nombre en negrita
        sb.append(" (ID: ").append(pcb.getId()).append(")");

        // Estado con color
        sb.append(" - Estado: <font color='").append(estadoColor).append("'><b>")
          .append(pcb.getStatus().toString()).append("</b></font>");

        // Ciclos y PC
        sb.append(" - Ciclos: ").append(pcb.getCiclosRestantes());
        sb.append(" (PC: ").append(pcb.getProgramCounter()).append(")");

        sb.append("</p>"); // Cerramos el párrafo

        current = current.getNext();
    }

    sb.append("</body></html>"); // Cerramos el HTML

    return sb.toString();
}

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }
    
    public void limpiarCola() {
    this.head = null;
    this.tail = null;
    this.size = 0;
}
    
}