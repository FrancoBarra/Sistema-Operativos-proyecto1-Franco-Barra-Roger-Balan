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
    
    public PCB verFrente() {
        if (isEmpty()) {
            return null;
        }
        return this.head.getPcb();
    }
    
    public Node getHead() {
        return this.head;
    }
    
    public PCB removerPorId(int pcbId) {
        Node current = this.head;
        Node previous = null;

        while (current != null) {
            if (current.getPcb().getId() == pcbId) {
                if (previous == null) {
                    this.head = current.getNext();
                    if (this.head == null) { 
                        this.tail = null;
                    }
                } else if (current == this.tail) {
                    previous.setNext(null);
                    this.tail = previous;
                } else {
                    previous.setNext(current.getNext());
                }

                this.size--;
                return current.getPcb();
            }
            previous = current;
            current = current.getNext();
        }
        return null; 
    }
    
    public boolean isEmpty() {
        return this.head == null;
    }

    public int getSize() {
        return size;
    }
    
    @Override
public String toString() {
    if (isEmpty()) {
        return "<html><body style='font-family: Segoe UI; font-size: 10pt; color: #888888;'><i>Cola vacía</i></body></html>";
    }

    StringBuilder sb = new StringBuilder();

    sb.append("<html><body style='font-family: Segoe UI; font-size: 10pt;'>");

    Node current = this.head;

    while (current != null) {
        PCB pcb = current.getPcb();

        String estadoColor = "#000000"; 
        if (pcb.getStatus() == ProcessStatus.READY) {
            estadoColor = "#008800"; 
        } else if (pcb.getStatus() == ProcessStatus.BLOCKED) {
            estadoColor = "#DD8800"; 
        } else if (pcb.getStatus() == ProcessStatus.TERMINATED) {
            estadoColor = "#CC0000"; 
        }

        sb.append("<p style='margin: 0; padding: 2px;'>"); 
        sb.append("<b>").append(pcb.getName()).append("</b>"); 
        sb.append(" (ID: ").append(pcb.getId()).append(")");

        sb.append(" - Estado: <font color='").append(estadoColor).append("'><b>")
          .append(pcb.getStatus().toString()).append("</b></font>");

        sb.append(" - Ciclos: ").append(pcb.getCiclosRestantes());
        sb.append(" (PC: ").append(pcb.getProgramCounter()).append(")");

        sb.append("</p>"); 

        current = current.getNext();
    }

    sb.append("</body></html>"); 

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
    public boolean buscarPorNombre(String nombre) {
    if (isEmpty()) {
        return false;
    }
    Node current = this.head;
    while (current != null) {
        if (current.getPcb().getName().equalsIgnoreCase(nombre)) {
            return true;
        }
        current = current.getNext();
    }
    return false;
}

public PCB get(int index) {
    if (index < 0 || index >= this.size) {
        return null; 
    }

    Node current = this.head;
    for (int i = 0; i < index; i++) {
        current = current.getNext();
    }

    return current.getPcb();
}

    
}