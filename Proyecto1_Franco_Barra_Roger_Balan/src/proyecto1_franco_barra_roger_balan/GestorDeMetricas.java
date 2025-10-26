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
    
    private static final int MAX_PROCESOS = 100;
    private final PCB[] pcbsTerminados;
    private int contadorTerminados;
    
    private long ciclosTotales = 0;
    private long ciclosCPUInactiva = 0;
    private long ciclosKernelCPU = 0;

    public GestorDeMetricas() {
        this.pcbsTerminados = new PCB[MAX_PROCESOS];
        this.contadorTerminados = 0;
    }
    
    public void agregarPCBTerminado(PCB pcb) {
        if (contadorTerminados < MAX_PROCESOS) {
            pcbsTerminados[contadorTerminados++] = pcb;
        }
    }
    
    public void actualizarMetricasGlobales(long relojActual, boolean estaCPUInactiva, boolean estaCPUEnModoKernel) {
        this.ciclosTotales = relojActual;
        if (estaCPUInactiva) {
            this.ciclosCPUInactiva++;
        }
        if (estaCPUEnModoKernel) {
            this.ciclosKernelCPU++;
        }
    }

    public double calcularThroughput() {
        if (ciclosTotales == 0) return 0.0;
        return (double) contadorTerminados / ciclosTotales;
    }
    
    public double calcularUtilizacionCPU() {
        if (ciclosTotales == 0) return 0.0;
        long ciclosUtilizados = ciclosTotales - ciclosCPUInactiva;
        return (double) ciclosUtilizados / ciclosTotales;
    }

    public double calcularTiempoDeRetornoPromedio() {
        if (contadorTerminados == 0) return 0.0;
        
        long tiempoRetornoTotal = 0;
        for (int i = 0; i < contadorTerminados; i++) {
            PCB pcb = pcbsTerminados[i];
            tiempoRetornoTotal += (pcb.getTiempoFinalizacion() - pcb.getArrivalTime()); 
        }
        return (double) tiempoRetornoTotal / contadorTerminados;
    }
    
    public double calcularTiempoDeEsperaPromedio() {
        if (contadorTerminados == 0) return 0.0;
        
        long tiempoEsperaTotal = 0;
        for (int i = 0; i < contadorTerminados; i++) {
            tiempoEsperaTotal += pcbsTerminados[i].getTiempoEsperaAcumulado(); 
        }
        return (double) tiempoEsperaTotal / contadorTerminados;
    }
    
    public double calcularTiempoDeRespuestaPromedio() {
        if (contadorTerminados == 0) return 0.0;
        
        long tiempoRespuestaTotal = 0;
        int contadorRespondidos = 0;
        
        for (int i = 0; i < contadorTerminados; i++) {
            PCB pcb = pcbsTerminados[i];
            if (pcb.getTiempoRespuesta() != -1) { 
                 tiempoRespuestaTotal += (pcb.getTiempoRespuesta() - pcb.getArrivalTime());
                 contadorRespondidos++;
            }
        }
        
        if (contadorRespondidos == 0) return 0.0;
        return (double) tiempoRespuestaTotal / contadorRespondidos;
    }
    
    public double calcularEquidad() {
        if (contadorTerminados <= 1) return 0.0;

        double mediaTR = calcularTiempoDeRetornoPromedio();
        if (mediaTR <= 0.0) return 0.0;

        double sumaDeDiferenciasCuadradas = 0.0;
        for (int i = 0; i < contadorTerminados; i++) {
            PCB pcb = pcbsTerminados[i];
            double tr = (pcb.getTiempoFinalizacion() - pcb.getArrivalTime());
            sumaDeDiferenciasCuadradas += Math.pow(tr - mediaTR, 2);
        }

        double varianza = sumaDeDiferenciasCuadradas / contadorTerminados;
        double desviacionEstandar = Math.sqrt(varianza);
        
        return desviacionEstandar / mediaTR;
    }
    
    public String obtenerResumenMetricas() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RESUMEN DE MÉTRICAS ===\n");
        sb.append("Procesos terminados: ").append(contadorTerminados).append("\n");
        sb.append("Ciclos totales: ").append(ciclosTotales).append("\n");
        sb.append("Throughput: ").append(String.format("%.4f", calcularThroughput())).append(" procesos/ciclo\n");
        sb.append("Utilización CPU: ").append(String.format("%.2f", calcularUtilizacionCPU() * 100)).append("%\n");
        sb.append("Tiempo retorno promedio: ").append(String.format("%.2f", calcularTiempoDeRetornoPromedio())).append(" ciclos\n");
        sb.append("Tiempo espera promedio: ").append(String.format("%.2f", calcularTiempoDeEsperaPromedio())).append(" ciclos\n");
        sb.append("Tiempo respuesta promedio: ").append(String.format("%.2f", calcularTiempoDeRespuestaPromedio())).append(" ciclos\n");
        sb.append("Equidad (CV): ").append(String.format("%.4f", calcularEquidad())).append("\n");
        
        return sb.toString();
    }
    
    public void limpiarMetricas() {
        for (int i = 0; i < contadorTerminados; i++) {
            pcbsTerminados[i] = null;
        }
        contadorTerminados = 0;
        ciclosTotales = 0;
        ciclosCPUInactiva = 0;
        ciclosKernelCPU = 0;
    }

    public long getCiclosTotales() { return ciclosTotales; }
    public int getContadorTerminados() { return contadorTerminados; }
    public PCB[] getPCBsTerminados() { return pcbsTerminados; }
    public long getCiclosCPUInactiva() { return ciclosCPUInactiva; }
    public long getCiclosKernelCPU() { return ciclosKernelCPU; }
}