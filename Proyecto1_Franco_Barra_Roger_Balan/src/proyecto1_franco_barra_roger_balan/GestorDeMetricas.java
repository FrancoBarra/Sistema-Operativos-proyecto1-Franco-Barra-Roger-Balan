/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;

/**
 * Clase GestorDeMetricas: Acumula datos estadísticos de la simulación.
 * Registra tiempos de CPU, tiempos de Kernel y acumula los PCBs terminados.
 */
public class GestorDeMetricas {

    // --- Contadores Globales ---
    private long ciclosKernel = 0;        // Tiempo que el SO pasó en planificación/dispatch/manejo de I/O.
    private long ciclosCPUInactiva = 0;   // Tiempo que la CPU pasó en estado Idle.
    
    // --- Almacenamiento de Terminados (Sin usar java.util.List) ---
    private static final int MAX_PROCESOS = 100;
    private final PCB[] pcbsTerminados;
    private int contadorTerminados; 

    public GestorDeMetricas() {
        this.pcbsTerminados = new PCB[MAX_PROCESOS];
        this.contadorTerminados = 0;
    }

    // =========================================================================
    // --- Incrementadores de Contadores Globales (Usados por SistemaOperativo) ---
    // =========================================================================

    /**
     * Incrementa el contador cuando el Kernel está trabajando (e.g., dispatch, interrupciones).
     */
    public void incrementarCiclosKernel() {
        this.ciclosKernel++;
    }

    /**
     * Incrementa el contador cuando la CPU está libre (no hay procesos listos).
     */
    public void incrementarCiclosCPUInactiva() {
        this.ciclosCPUInactiva++;
    }

    // =========================================================================
    // --- Gestión de Procesos Terminados (Usado por SistemaOperativo) ---
    // =========================================================================
    
    /**
     * Registra un PCB terminado para el cálculo de métricas finales.
     * @param pcb El proceso cuyo estado es TERMINATED.
     */
    public void agregarPCBTerminado(PCB pcb) {
        if (pcb.getStatus() == ProcessStatus.TERMINATED && contadorTerminados < MAX_PROCESOS) {
            this.pcbsTerminados[contadorTerminados] = pcb;
            this.contadorTerminados++;
        }
    }
    
    // =========================================================================
    // --- Getters de Métricas (Para la GUI o Reportes) ---
    // =========================================================================

    public long getCiclosKernel() {
        return ciclosKernel;
    }

    public long getCiclosCPUInactiva() {
        return ciclosCPUInactiva;
    }

    public int getContadorTerminados() {
        return contadorTerminados;
    }

    public PCB[] getPcbsTerminados() {
        return pcbsTerminados; // Retorna el array completo
    }

    // =========================================================================
    // --- Métodos de Cálculo de Métricas Finales ---
    // =========================================================================
    
    /**
     * **Métrica de Utilización de CPU**
     * Porcentaje de tiempo que la CPU estuvo ejecutando (Modo Usuario).
     */
    public double calcularUtilizacionCPU(long ciclosTotales) {
        if (ciclosTotales == 0) return 0.0;
        
        // Tiempo en Modo Usuario = Ciclos Totales - (Kernel Time + Idle Time)
        long ciclosUsuario = ciclosTotales - ciclosKernel - ciclosCPUInactiva;
        return (double) ciclosUsuario / ciclosTotales;
    }
    
    /**
     * **Métrica de Tiempo de Retorno Promedio (Turnaround Time)**
     * Tiempo promedio que un proceso tardó en completarse (desde llegada hasta terminación).
     */
    public double calcularTiempoDeRetornoPromedio() {
        if (contadorTerminados == 0) return 0.0;
        
        long tiempoTotal = 0;
        for (int i = 0; i < contadorTerminados; i++) {
            PCB pcb = pcbsTerminados[i];
            tiempoTotal += pcb.getTurnaroundTime(); 
        }
        return (double) tiempoTotal / contadorTerminados;
    }
    
    /**
     * **Métrica de Tiempo de Espera Promedio (Waiting Time)**
     * Tiempo promedio que un proceso pasó en la Ready Queue.
     */
    public double calcularTiempoDeEsperaPromedio() {
        if (contadorTerminados == 0) return 0.0;
        
        long tiempoTotal = 0;
        for (int i = 0; i < contadorTerminados; i++) {
            PCB pcb = pcbsTerminados[i];
            tiempoTotal += pcb.getTiempoEsperaAcumulado(); 
        }
        return (double) tiempoTotal / contadorTerminados;
    }
    
    /**
     * **Métrica de Tiempo de Respuesta Promedio (Response Time)**
     * Tiempo promedio desde la llegada hasta la primera ejecución (First Run).
     */
    public double calcularTiempoDeRespuestaPromedio() {
        if (contadorTerminados == 0) return 0.0;
        
        long tiempoRespuestaTotal = 0;
        int contadorRespondidos = 0;
        
        for (int i = 0; i < contadorTerminados; i++) {
            PCB pcb = pcbsTerminados[i];
            // Solo se cuenta si el tiempo de respuesta fue registrado (tiempoRespuesta > 0)
            if (pcb.getTiempoRespuesta() > 0) {
                 // Tiempo de Respuesta = Primera Ejecución - Llegada
                 tiempoRespuestaTotal += (pcb.getTiempoRespuesta() - pcb.getArrivalTime());
                 contadorRespondidos++;
            }
        }
        if (contadorRespondidos == 0) return 0.0;
        return (double) tiempoRespuestaTotal / contadorRespondidos;
    }
}