/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 *
 * @author frank
 */
// Archivo: ConfiguracionManager.java

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Clase ConfiguracionManager: Gestiona la lectura de la configuración de la simulación
 * desde un archivo CSV usando solo librerías de java.io.* (básicas).
 */
public class ConfiguracionManager {

    private final String fileName;
    // Establecemos un límite fijo para el array de procesos (sin List)
    private static final int MAX_PROCESSES = 100; 

    public ConfiguracionManager(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Carga la configuración del sistema y los procesos desde el archivo CSV.
     * @return Un objeto SimulacionConfig que contiene la duración y el array de PCBs.
     * @throws IOException Si ocurre un error de lectura de archivo.
     */
    public SimulacionConfig cargarConfiguracion() throws IOException {
        long cycleDuration = 100; // Valor por defecto
        PCB[] pcbArray = new PCB[MAX_PROCESSES];
        int pcbCount = 0;
        
        BufferedReader reader = null;
        try {
            // Usamos FileReader y BufferedReader (de java.io) para lectura básica
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            
            // --- 1. Leer Duración del Ciclo ---
            line = reader.readLine();
            if (line != null && line.startsWith("duration:")) {
                String durationValue = line.substring("duration:".length()).trim();
                try {
                    // Long.parseLong es de java.lang
                    cycleDuration = Long.parseLong(durationValue);
                } catch (NumberFormatException e) {
                    System.err.println("Advertencia: Duración no válida. Usando 100ms.");
                }
            }
            
            // --- 2. Saltar encabezado ---
            reader.readLine(); 
            
            // --- 3. Leer Procesos ---
            while ((line = reader.readLine()) != null) {
                if (pcbCount >= MAX_PROCESSES) {
                    System.err.println("Advertencia: Límite máximo de procesos alcanzado (" + MAX_PROCESSES + ")");
                    break;
                }
                
                // Procesar la línea CSV
                PCB pcb = parseProcessLine(line, pcbCount);
                if (pcb != null) {
                    pcbArray[pcbCount++] = pcb;
                }
            }
        } finally {
            // Asegurar que el lector se cierre
            if (reader != null) {
                reader.close();
            }
        }
        
        // Crear un array final del tamaño exacto (el método más seguro sin util.Arrays.copyOf)
        PCB[] finalPcbArray = new PCB[pcbCount];
        for (int i = 0; i < pcbCount; i++) {
            finalPcbArray[i] = pcbArray[i];
        }
        
        return new SimulacionConfig(cycleDuration, finalPcbArray);
    }
    
    /**
     * Parsea una sola línea CSV de forma manual sin usar String.split() de java.util.
     * @param line La línea del archivo CSV (nombre,instr,tipo,ciclosExcep,ciclosIO).
     * @return El PCB creado.
     */
    private PCB parseProcessLine(String line, int index) {
        // Implementación manual de split por coma
        String[] parts = new String[5];
        int partIndex = 0;
        int lastComma = -1;
        
        for (int i = 0; i < line.length() && partIndex < 5; i++) {
            if (line.charAt(i) == ',') {
                // String.substring es de java.lang
                parts[partIndex++] = line.substring(lastComma + 1, i).trim();
                lastComma = i;
            }
        }
        // Capturar el último elemento
        if (partIndex < 5) {
            parts[partIndex] = line.substring(lastComma + 1).trim();
            partIndex++;
        }
        
        if (partIndex < 5) {
            System.err.println("Error: Línea CSV incompleta para proceso " + (index + 1) + ": " + line);
            return null;
        }

        try {
            String name = parts[0];
            int instructions = Integer.parseInt(parts[1]);
            String typeStr = parts[2];
            int cyclesForExcep = Integer.parseInt(parts[3]);
            int cyclesToSatisfy = Integer.parseInt(parts[4]);

            // Convertir String a Enum (ProcessType)
            ProcessType type = ProcessType.CPU_BOUND;
            if (typeStr.equalsIgnoreCase("IO_BOUND")) {
                type = ProcessType.IO_BOUND;
            }
            
            // Tiempo de llegada (arrivalTime) es el ciclo 0 al cargarse
            return new PCB(name, instructions, type, cyclesForExcep, cyclesToSatisfy, 0);

        } catch (NumberFormatException e) {
            System.err.println("Error de formato numérico en línea para proceso " + (index + 1) + ": " + line);
            return null;
        }
    }
}
