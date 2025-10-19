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
                } 
                else if (current == this.tail) {
                    previous.setNext(null);
                    this.tail = previous;
                }
                else {
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
}