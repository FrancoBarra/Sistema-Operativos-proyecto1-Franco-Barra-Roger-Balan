/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;
import java.awt.Dimension;
/**
 * @author frank
 */
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import java.awt.BorderLayout;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.PlotOrientation;
import java.io.File; 
import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
public class GUI extends javax.swing.JFrame {
    
   private SistemaOperativo so;
    private javax.swing.Timer simulationTimer;
    private String rutaArchivoCSV;
    private Cola procesosEnStaging;
    

private DefaultPieDataset pieDataset;
private JFreeChart pieChart;
private ChartPanel pieChartPanel; 


private DefaultCategoryDataset timeDataset;
private JFreeChart timeBarChart;
private ChartPanel timeChartPanel;


private DefaultCategoryDataset queueDataset;
private JFreeChart queueBarChart;
private ChartPanel queueChartPanel;
    
    
    
    public GUI() {
    initComponents();

jPanelGraficos.setLayout(new java.awt.GridLayout(1, 3, 5, 5));


this.pieDataset = new DefaultPieDataset();
this.pieDataset.setValue("CPU Ocioso", 100.0);
this.pieDataset.setValue("CPU en Uso", 0.0);

this.pieChart = ChartFactory.createPieChart(
    "Utilización de CPU", pieDataset, true, true, false
);

this.pieChartPanel = new ChartPanel(pieChart); 

jPanelGraficos.add(this.pieChartPanel);


this.timeDataset = new DefaultCategoryDataset();
this.timeDataset.setValue(0, "Ciclos", "Espera");
this.timeDataset.setValue(0, "Ciclos", "Retorno");
this.timeDataset.setValue(0, "Ciclos", "Respuesta");

this.timeBarChart = ChartFactory.createBarChart(
    "Tiempos Promedio",  
    "Métrica",          
    "Ciclos",            
    this.timeDataset,
    PlotOrientation.VERTICAL,
    false, true, false
);
this.timeChartPanel = new ChartPanel(this.timeBarChart);
// Añadimos el segundo gráfico
jPanelGraficos.add(this.timeChartPanel);


this.queueDataset = new DefaultCategoryDataset();
this.queueDataset.setValue(0, "Procesos", "Listos");
this.queueDataset.setValue(0, "Procesos", "Bloqueados");
this.queueDataset.setValue(0, "Procesos", "Terminados");

this.queueBarChart = ChartFactory.createBarChart(
    "Procesos en Cola", 
    "Colas",          
    "Cantidad",     
    this.queueDataset,
    PlotOrientation.VERTICAL,
    false, true, false
);
this.queueChartPanel = new ChartPanel(this.queueBarChart);

jPanelGraficos.add(this.queueChartPanel);


jPanelGraficos.validate(); 

    txtPaneCPU.setContentType("text/html");
    txtPaneCPU.setContentType("text/html");
    txtPaneCPU.setBackground(new java.awt.Color(240, 240, 240));
    txtPaneCPU.setText("<html><body style='font-family: Segoe UI; font-size: 10pt; color: #888888;'><i>Inactivo</i></body></html>");
   
    txtAreaReady.setContentType("text/html");
    
    txtAreaBlocked.setContentType("text/html");
    txtAreaTerminados.setContentType("text/html");


    for (Scheduler.SchedulingAlgorithm algo : Scheduler.SchedulingAlgorithm.values()) {
        cmbAlgoritmo.addItem(algo.toString()); 
    }


    cmbTipoProceso.addItem(ProcessType.CPU_BOUND);
    cmbTipoProceso.addItem(ProcessType.IO_BOUND);


    this.procesosEnStaging = new Cola();

   
    spinnerCicloMs.setValue(100);
    
    spinnerQuantum.setValue(5);

  
    try {
      
    } catch (Exception e) {
        
    }


    btnIniciar.setEnabled(false); 
    btnPausar.setEnabled(false);
    btnDetener.setEnabled(false);
    btnAnadirProceso.setEnabled(true); 

  

  
    spinnerCiclosExcep.setEnabled(false);
    spinnerCiclosIO.setEnabled(false);
    
    cmbTipoProceso.addItemListener(new java.awt.event.ItemListener() {
    public void itemStateChanged(java.awt.event.ItemEvent evt) {

       
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {

         
            String itemSeleccionado = evt.getItem().toString();

      
            boolean esIOBound = itemSeleccionado.equals(ProcessType.IO_BOUND.toString());
          

           
            spinnerCiclosExcep.setEnabled(esIOBound);
            spinnerCiclosIO.setEnabled(esIOBound);

           
            if (!esIOBound) {
                spinnerCiclosExcep.setValue(0);
                spinnerCiclosIO.setValue(0);
            }
        }
    }
});
    }
    
    private void actualizarVistasGUI() {
        if (so == null) return;

  
        lblRelojGlobal.setText(String.valueOf(so.getGlobalClock()));

     
       PCB pcbEnCPU = so.getCpu().getCurrentProcess();

        if (pcbEnCPU != null) {
            
            lblRelojGlobal1.setText("Modo: Usuario");
           
            StringBuilder sb = new StringBuilder();
            
          
            sb.append("<html><body style='font-family: Segoe UI; font-size: 10pt;'>");

           
            sb.append("<b>").append(pcbEnCPU.getName()).append("</b>");
            sb.append(" (ID: ").append(pcbEnCPU.getId()).append(")<br>"); 

           
            sb.append("PC: <b>").append(pcbEnCPU.getProgramCounter()).append("</b>");
            sb.append(" / MAR: <b>").append(pcbEnCPU.getMemoryAddressRegister()).append("</b><br>");

      
            sb.append("Ciclos Restantes: <b>").append(pcbEnCPU.getCiclosRestantes()).append("</b>");

         
            sb.append("</body></html>");
            
          
            txtPaneCPU.setText(sb.toString());

        } else {
            
            lblRelojGlobal1.setText("Modo: Kernel");
           
            txtPaneCPU.setText("<html><body style='font-family: Segoe UI; font-size: 10pt; color: #888888;'><i>Inactivo</i></body></html>");
        }

        
        txtAreaReady.setText(so.getReadyQueue().toString());
        txtAreaBlocked.setText(so.getBlockedQueue().toString());
        txtAreaTerminados.setText(so.getTerminatedQueue().toString());
        actualizarGraficoCPU();
        
        actualizarMetricasGUI();
       
        actualizarGraficosBarras();
    }


 
    private void actualizarMetricasGUI() {
    if (so == null || so.getGestorMetricas() == null) {
        return;
    }

    GestorDeMetricas metricas = so.getGestorMetricas();

    lblThroughput.setText(String.format("%.4f", metricas.calcularThroughput()));
    lblUtilizacionCPU.setText(String.format("%.2f %%", metricas.calcularUtilizacionCPU() * 100));
    lblTurnaround.setText(String.format("%.2f", metricas.calcularTiempoDeRetornoPromedio()));
    lblEspera.setText(String.format("%.2f", metricas.calcularTiempoDeEsperaPromedio()));
    lblRespuesta.setText(String.format("%.2f", metricas.calcularTiempoDeRespuestaPromedio()));
}
    private void mostrarMetricasFinales() {
        if (so == null) return;
        
        
        
        GestorDeMetricas metricas = so.getGestorMetricas();
        
        
        
 
        lblThroughput.setText(String.format("%.4f", metricas.calcularThroughput()));
        lblUtilizacionCPU.setText(String.format("%.2f %%", metricas.calcularUtilizacionCPU() * 100));
        lblTurnaround.setText(String.format("%.2f", metricas.calcularTiempoDeRetornoPromedio()));
        lblEspera.setText(String.format("%.2f", metricas.calcularTiempoDeEsperaPromedio()));
        lblRespuesta.setText(String.format("%.2f", metricas.calcularTiempoDeRespuestaPromedio()));

    
        btnIniciar.setEnabled(true);
        btnCargarCSV.setEnabled(true);
        btnPausar.setEnabled(false);
        btnDetener.setEnabled(false);
        
        javax.swing.JOptionPane.showMessageDialog(this, "¡Simulación Finalizada!");
    }


    private void actualizarGraficoCPU() {
   
        if (so == null || so.getGestorMetricas() == null) {
            return;
        }
        
        

  
        double utilizacion = so.getGestorMetricas().calcularUtilizacionCPU(); 

     
        double porcentajeUso = utilizacion * 100.0;
        double porcentajeOcioso = (1.0 - utilizacion) * 100.0;

 
        this.pieDataset.setValue("CPU en Uso", porcentajeUso);
        this.pieDataset.setValue("CPU Ocioso", porcentajeOcioso);
    }
    
  
private void actualizarGraficosBarras() {
    if (so == null || so.getGestorMetricas() == null) {
        return;
    }
    
    
    GestorDeMetricas metricas = so.getGestorMetricas();
    this.timeDataset.setValue(metricas.calcularTiempoDeEsperaPromedio(), "Ciclos", "Espera");
    this.timeDataset.setValue(metricas.calcularTiempoDeRetornoPromedio(), "Ciclos", "Retorno");
    this.timeDataset.setValue(metricas.calcularTiempoDeRespuestaPromedio(), "Ciclos", "Respuesta");

  
    this.queueDataset.setValue(so.getReadyQueue().getSize(), "Procesos", "Listos");
    this.queueDataset.setValue(so.getBlockedQueue().getSize(), "Procesos", "Bloqueados");
    this.queueDataset.setValue(so.getTerminatedQueue().getSize(), "Procesos", "Terminados");
}

 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbAlgoritmo = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        spinnerCicloMs = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spinnerQuantum = new javax.swing.JSpinner();
        btnIniciar = new javax.swing.JButton();
        btnPausar = new javax.swing.JButton();
        btnDetener = new javax.swing.JButton();
        btnGuardarConfig = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblRelojGlobal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtAreaBlocked = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtAreaReady = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaTerminados = new javax.swing.JTextPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtPaneCPU = new javax.swing.JTextPane();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lblRelojGlobal1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        lblThroughput = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblUtilizacionCPU = new javax.swing.JLabel();
        lblTurnaround = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        lblEspera = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        lblRespuesta = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btnCargarCSV = new javax.swing.JButton();
        txtRutaCSV = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        nombre = new javax.swing.JLabel();
        txtNombreProceso = new javax.swing.JTextField();
        Instrucciones = new javax.swing.JLabel();
        spinnerInstrucciones = new javax.swing.JSpinner();
        tipo = new javax.swing.JLabel();
        cmbTipoProceso = new javax.swing.JComboBox<>();
        CiclospExcepcion = new javax.swing.JLabel();
        spinnerCiclosExcep = new javax.swing.JSpinner();
        jLabel5 = new javax.swing.JLabel();
        spinnerCiclosIO = new javax.swing.JSpinner();
        btnAnadirProceso = new javax.swing.JButton();
        jPanelGraficos = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(99, 128, 169));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(102, 255, 255), null));
        jPanel1.setForeground(new java.awt.Color(100, 100, 150));

        jLabel1.setText("Algoritmo");

        cmbAlgoritmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbAlgoritmoActionPerformed(evt);
            }
        });

        jLabel2.setText("Duracion Ciclo (ms)");

        spinnerCicloMs.setModel(new javax.swing.SpinnerNumberModel(100, 1, null, 1));
        spinnerCicloMs.setMinimumSize(new java.awt.Dimension(100, 100));

        jLabel3.setText("Quantum (Para RR)");

        spinnerQuantum.setModel(new javax.swing.SpinnerNumberModel(5, 1, null, 1));

        btnIniciar.setText("Iniciar");
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        btnPausar.setText("Pausar");
        btnPausar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPausarActionPerformed(evt);
            }
        });

        btnDetener.setText("Detener");
        btnDetener.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetenerActionPerformed(evt);
            }
        });

        btnGuardarConfig.setText("Guardar");
        btnGuardarConfig.setAlignmentY(0.0F);
        btnGuardarConfig.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGuardarConfig.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarConfigActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbAlgoritmo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spinnerCicloMs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addGap(60, 60, 60))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(spinnerQuantum, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                                .addComponent(btnGuardarConfig)
                                .addGap(14, 14, 14)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDetener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnIniciar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnPausar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(41, 41, 41)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbAlgoritmo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spinnerCicloMs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(btnIniciar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spinnerQuantum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPausar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardarConfig)
                    .addComponent(btnDetener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel3.setBackground(new java.awt.Color(99, 128, 169));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.cyan, null));

        jLabel4.setText("Reloj Global");

        lblRelojGlobal.setText("0");

        jLabel9.setText("Cola Bloqueados");

        jLabel10.setText("Cola Terminados");

        jLabel11.setText("Cola Ready");

        txtAreaBlocked.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane1.setViewportView(txtAreaBlocked);

        txtAreaReady.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane3.setViewportView(txtAreaReady);

        txtAreaTerminados.setBackground(new java.awt.Color(204, 204, 204));
        jScrollPane2.setViewportView(txtAreaTerminados);

        jScrollPane4.setViewportView(txtPaneCPU);

        jLabel7.setText("CPU");

        jLabel6.setText("Modo");

        lblRelojGlobal1.setText("Esperando");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(195, 195, 195)
                                .addComponent(jLabel10))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(203, 203, 203)
                                .addComponent(jLabel9))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(210, 210, 210)
                                .addComponent(jLabel11))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(211, 211, 211)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblRelojGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblRelojGlobal1, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(274, 274, 274)
                .addComponent(jLabel7)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(lblRelojGlobal, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblRelojGlobal1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40))
        );

        jPanel4.setBackground(new java.awt.Color(99, 128, 169));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.cyan, null));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel12.setText("Throughput");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(63, 46, 0, 0);
        jPanel4.add(jLabel12, gridBagConstraints);

        lblThroughput.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 66, 0, 0);
        jPanel4.add(lblThroughput, gridBagConstraints);

        jLabel14.setText("TurnAround Promedio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(60, 24, 0, 20);
        jPanel4.add(jLabel14, gridBagConstraints);

        jLabel15.setText("Utilizacion CPU");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(51, 46, 0, 0);
        jPanel4.add(jLabel15, gridBagConstraints);

        lblUtilizacionCPU.setText("0.0%");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 68, 0, 0);
        jPanel4.add(lblUtilizacionCPU, gridBagConstraints);

        lblTurnaround.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 72, 0, 0);
        jPanel4.add(lblTurnaround, gridBagConstraints);

        jLabel18.setText("Espera Promedio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(36, 34, 0, 0);
        jPanel4.add(jLabel18, gridBagConstraints);

        lblEspera.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(27, 67, 0, 0);
        jPanel4.add(lblEspera, gridBagConstraints);

        jLabel20.setText("Respuesta Promedio");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(47, 24, 0, 0);
        jPanel4.add(jLabel20, gridBagConstraints);

        lblRespuesta.setText("0.0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 70, 31, 0);
        jPanel4.add(lblRespuesta, gridBagConstraints);

        jPanel2.setBackground(new java.awt.Color(99, 128, 169));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.cyan, null));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnCargarCSV.setText("Cargar CSV");
        btnCargarCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarCSVActionPerformed(evt);
            }
        });
        jPanel2.add(btnCargarCSV, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 8, 370, -1));

        txtRutaCSV.setBackground(new java.awt.Color(204, 204, 204));
        txtRutaCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRutaCSVActionPerformed(evt);
            }
        });
        jPanel2.add(txtRutaCSV, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 37, 370, 78));

        jPanel5.setBackground(new java.awt.Color(99, 128, 169));

        nombre.setText("Ingrese Nombre:");

        txtNombreProceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNombreProcesoActionPerformed(evt);
            }
        });

        Instrucciones.setText("Instrucciones");

        spinnerInstrucciones.setModel(new javax.swing.SpinnerNumberModel(50, 1, null, 1));

        tipo.setText("Tipo");

        cmbTipoProceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoProcesoActionPerformed(evt);
            }
        });

        CiclospExcepcion.setText("Ciclos p/ Excepción:");

        spinnerCiclosExcep.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));

        jLabel5.setText("Ciclos p/ Satisfacer E/S");

        spinnerCiclosIO.setModel(new javax.swing.SpinnerNumberModel());

        btnAnadirProceso.setText("Añadir Proceso");
        btnAnadirProceso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAnadirProcesoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nombre)
                    .addComponent(txtNombreProceso, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(spinnerInstrucciones, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(Instrucciones, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(cmbTipoProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(67, 67, 67)
                        .addComponent(spinnerCiclosExcep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(86, 86, 86)
                        .addComponent(spinnerCiclosIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35)
                        .addComponent(btnAnadirProceso)
                        .addContainerGap())
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(tipo)
                        .addGap(72, 72, 72)
                        .addComponent(CiclospExcepcion)
                        .addGap(35, 35, 35)
                        .addComponent(jLabel5)
                        .addGap(122, 122, 122))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nombre)
                    .addComponent(Instrucciones)
                    .addComponent(tipo)
                    .addComponent(CiclospExcepcion)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNombreProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerInstrucciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoProceso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerCiclosExcep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spinnerCiclosIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAnadirProceso))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        jPanelGraficos.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelGraficos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanelGraficos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtRutaCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRutaCSVActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtRutaCSVActionPerformed

    private void cmbAlgoritmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAlgoritmoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAlgoritmoActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        try {
      
        String algoSeleccionado = (String) cmbAlgoritmo.getSelectedItem();
        Scheduler.SchedulingAlgorithm algoritmo = Scheduler.SchedulingAlgorithm.valueOf(algoSeleccionado);
        int quantum = (Integer) spinnerQuantum.getValue();
        int cicloMs = (Integer) spinnerCicloMs.getValue();

       
        Cola readyQueue = new Cola();
        Cola blockedQueue = new Cola();
        Cola suspendedReadyQueue = new Cola(); 
        
        Scheduler scheduler = new Scheduler(readyQueue, algoritmo, quantum);
        GestorEstados gestorEstados = new GestorEstados(readyQueue, blockedQueue, suspendedReadyQueue, scheduler);
        CPU cpu = new CPU(quantum); // 
        // ------------------------------------------

     
        this.so = new SistemaOperativo(cpu, scheduler, gestorEstados, readyQueue, blockedQueue);
        
       
        txtAreaReady.setText(""); 
        for (int i = 0; i < procesosEnStaging.getSize(); i++) {
            
           
            PCB pcbOriginal = procesosEnStaging.get(i);
            
         
            PCB pcbClonado = new PCB(pcbOriginal); 
            
            pcbClonado.setStatus(ProcessStatus.READY);
            so.getScheduler().reinsertProcess(pcbClonado); 
        }

       
        so.setCycleDuration(cicloMs); 
        this.simulationTimer = new javax.swing.Timer(cicloMs, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (so.isSimulationFinished()) {
                    simulationTimer.stop();
                    mostrarMetricasFinales();
                } else {
                    so.avanzarCiclo(); 
                    actualizarVistasGUI(); 
                }
            }
        });
        simulationTimer.start();
        
      
        btnIniciar.setEnabled(false);
        btnAnadirProceso.setEnabled(true); 
        btnPausar.setEnabled(true);
        btnDetener.setEnabled(true);
        btnCargarCSV.setEnabled(false);
        btnGuardarConfig.setEnabled(false); 
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error al iniciar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); 
    }
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
     
        if (this.simulationTimer == null) {
        return;
    }


    if (this.simulationTimer.isRunning()) {
    
        this.simulationTimer.stop();

      
        btnPausar.setText("Reanudar");

    } else {
 
        this.simulationTimer.start();

     
        btnPausar.setText("Pausar");
    }
    }//GEN-LAST:event_btnPausarActionPerformed

    private void btnCargarCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarCSVActionPerformed
  
    JFileChooser fileChooser = new JFileChooser();
    int returnValue = fileChooser.showOpenDialog(this);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        txtRutaCSV.setText(selectedFile.getAbsolutePath());
        
      
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            
       
            String configLine = reader.readLine();
            if (configLine == null) {
                throw new Exception("El archivo está vacío.");
            }
            
            String[] parts = configLine.split(",");
            if (parts.length < 3) {
                throw new Exception("La línea de configuración es inválida.");
            }
            
            // ¡Actualizamos la GUI con la configuración!
            cmbAlgoritmo.setSelectedItem(parts[0]);
            spinnerCicloMs.setValue(Integer.parseInt(parts[1]));
            spinnerQuantum.setValue(Integer.parseInt(parts[2]));
            
         
            procesosEnStaging.limpiarCola(); 
            String processLine;
            int procesosCargados = 0;
            
         
            while ((processLine = reader.readLine()) != null) {
                String[] pcbParts = processLine.split(",");
                if (pcbParts.length < 5) continue; 
                
               
                PCB newPcb = new PCB(
                        pcbParts[0],                           
                        Integer.parseInt(pcbParts[1]),            
                        ProcessType.valueOf(pcbParts[2]),          
                        Integer.parseInt(pcbParts[3]),           
                        Integer.parseInt(pcbParts[4]),           
                        0                                          
                );
                
                newPcb.setStatus(ProcessStatus.NEW);
                procesosEnStaging.agregar(newPcb);
                procesosCargados++;
            }
            
          
            if (procesosCargados > 0) {
                btnIniciar.setEnabled(true);
            }
            txtAreaReady.setText(procesosEnStaging.toString()); 
            
            JOptionPane.showMessageDialog(this,
                    "¡Configuración cargada y " + procesosCargados + " procesos añadidos!",
                    "Carga Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar el archivo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    }//GEN-LAST:event_btnCargarCSVActionPerformed

    private void btnDetenerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetenerActionPerformed
    if (this.simulationTimer != null) this.simulationTimer.stop();
    if (this.so != null) this.so.stopSimulation();

  
    lblRelojGlobal.setText("0");
    txtAreaReady.setText("");
    txtAreaBlocked.setText("");
    txtAreaTerminados.setText("");
  


   
    btnIniciar.setEnabled(false); 
    btnAnadirProceso.setEnabled(true);
    btnPausar.setEnabled(false);
    btnDetener.setEnabled(false);
    btnCargarCSV.setEnabled(true); 
    btnGuardarConfig.setEnabled(true);

   
    this.so = null;
    this.simulationTimer = null; 

    JOptionPane.showMessageDialog(this, "Simulación detenida.");
    }//GEN-LAST:event_btnDetenerActionPerformed

    private void btnGuardarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarConfigActionPerformed
     // (Asegúrate de importar java.io.FileWriter y java.io.IOException)
    try (java.io.FileWriter writer = new java.io.FileWriter("sim_config.csv")) {
        
      
        String algoritmo = (String) cmbAlgoritmo.getSelectedItem();
        int cicloMs = (Integer) spinnerCicloMs.getValue();
        int quantum = (Integer) spinnerQuantum.getValue();
        
        
        writer.write(algoritmo + "," + cicloMs + "," + quantum + "\n");

        
        for (int i = 0; i < procesosEnStaging.getSize(); i++) {
            PCB pcb = procesosEnStaging.get(i);
            
          
            String pcbLine = String.format("%s,%d,%s,%d,%d\n",
                    pcb.getName(),
                    pcb.getLongitudPrograma(),
                    pcb.getType().toString(),
                    pcb.getCiclosParaExcepcion(),
                    pcb.getCiclosParaSatisfacerIO()
            );
            writer.write(pcbLine); 
        }
        
      
        JOptionPane.showMessageDialog(this,
                "¡Configuración y " + procesosEnStaging.getSize() + " procesos guardados!",
                "Guardado",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (java.io.IOException e) {
        JOptionPane.showMessageDialog(this,
                "Error al guardar la configuración: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
    }//GEN-LAST:event_btnGuardarConfigActionPerformed

    private void cmbTipoProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoProcesoActionPerformed
       
    }//GEN-LAST:event_cmbTipoProcesoActionPerformed

    private void btnAnadirProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirProcesoActionPerformed
        try {
        
        String nombre = txtNombreProceso.getText();
        int instrucciones = (Integer) spinnerInstrucciones.getValue();
        ProcessType tipo = (ProcessType) cmbTipoProceso.getSelectedItem();
        int ciclosExcep = (Integer) spinnerCiclosExcep.getValue();
        int ciclosIO = (Integer) spinnerCiclosIO.getValue();

      
        if (nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (procesosEnStaging.buscarPorNombre(nombre)) {
            JOptionPane.showMessageDialog(this, "Error: Ya existe un proceso con ese nombre en la lista de espera.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (so != null && (so.getReadyQueue().buscarPorNombre(nombre) || 
                            so.getBlockedQueue().buscarPorNombre(nombre) ||
                            so.getTerminatedQueue().buscarPorNombre(nombre))) {
            JOptionPane.showMessageDialog(this, "Error: Ya existe un proceso con ese nombre en la simulación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

       
        long arrivalTime = (so != null) ? so.getGlobalClock() : 0;
        PCB newPcb = new PCB(nombre, instrucciones, tipo, ciclosExcep, ciclosIO, arrivalTime);

     
        if (so == null || !simulationTimer.isRunning()) {
          
            newPcb.setStatus(ProcessStatus.NEW);
            procesosEnStaging.agregar(newPcb);
            btnIniciar.setEnabled(true); 

           
            txtAreaReady.setText(procesosEnStaging.toString());

        } else {
           
            newPcb.setStatus(ProcessStatus.READY);
            so.getScheduler().reinsertProcess(newPcb);
        }

       
        txtNombreProceso.setText("");
        spinnerInstrucciones.setValue(50); 

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error creando proceso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnAnadirProcesoActionPerformed

    private void txtNombreProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreProcesoActionPerformed
        
    }//GEN-LAST:event_txtNombreProcesoActionPerformed

    
    public static void main(String args[]) {
      
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>


    
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CiclospExcepcion;
    private javax.swing.JLabel Instrucciones;
    private javax.swing.JButton btnAnadirProceso;
    private javax.swing.JButton btnCargarCSV;
    private javax.swing.JButton btnDetener;
    private javax.swing.JButton btnGuardarConfig;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnPausar;
    private javax.swing.JComboBox<String> cmbAlgoritmo;
    private javax.swing.JComboBox<ProcessType> cmbTipoProceso;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanelGraficos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblEspera;
    private javax.swing.JLabel lblRelojGlobal;
    private javax.swing.JLabel lblRelojGlobal1;
    private javax.swing.JLabel lblRespuesta;
    private javax.swing.JLabel lblThroughput;
    private javax.swing.JLabel lblTurnaround;
    private javax.swing.JLabel lblUtilizacionCPU;
    private javax.swing.JLabel nombre;
    private javax.swing.JSpinner spinnerCicloMs;
    private javax.swing.JSpinner spinnerCiclosExcep;
    private javax.swing.JSpinner spinnerCiclosIO;
    private javax.swing.JSpinner spinnerInstrucciones;
    private javax.swing.JSpinner spinnerQuantum;
    private javax.swing.JLabel tipo;
    private javax.swing.JTextPane txtAreaBlocked;
    private javax.swing.JTextPane txtAreaReady;
    private javax.swing.JTextPane txtAreaTerminados;
    private javax.swing.JTextField txtNombreProceso;
    private javax.swing.JTextPane txtPaneCPU;
    private javax.swing.JTextField txtRutaCSV;
    // End of variables declaration//GEN-END:variables
}
