/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package proyecto1_franco_barra_roger_balan;
import java.awt.Dimension;
/**
 *
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
// (Los JTextPane, JLabel, etc. no se importan aquí)

// --- Imports de AWT (para la nueva línea 'Dimension') ---


// --- Imports de IO (para cargar el archivo) ---
import java.io.File; // <-- ¡NUEVO!
import java.io.BufferedReader; // <-- ¡NUEVO!
import java.io.FileReader; // <-- ¡NUEVO!
import java.io.FileNotFoundException; // <-- ¡NUEVO!

// (Probablemente también necesites estos para los botones)
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
public class GUI extends javax.swing.JFrame {
    
   private SistemaOperativo so;
    private javax.swing.Timer simulationTimer;
    private String rutaArchivoCSV;
    private Cola procesosEnStaging;
    
    // --- NUEVAS VARIABLES PARA EL GRÁFICO ---
 // --- VARIABLES PARA LOS GRÁFICOS ---
private DefaultPieDataset pieDataset;
private JFreeChart pieChart;
private ChartPanel pieChartPanel; // <-- CAMBIÉ EL NOMBRE (antes 'chartPanel')

 // --- ¡NUEVAS VARIABLES! (Para Gráfico de Barras 1: Tiempos) ---
private DefaultCategoryDataset timeDataset;
private JFreeChart timeBarChart;
private ChartPanel timeChartPanel;

// --- ¡NUEVAS VARIABLES! (Para Gráfico de Barras 2: Colas) ---
private DefaultCategoryDataset queueDataset;
private JFreeChart queueBarChart;
private ChartPanel queueChartPanel;
    
    
    
    public GUI() {
    initComponents();
    // PEGA ESTE CÓDIGO NUEVO EN LA LÍNEA 64

// --- INICIALIZAR LOS 3 GRÁFICOS ---
        
// 1. Establecer el Layout de 3 columnas en el panel principal
// (1 fila, 3 columnas, con 5px de espacio entre ellas)
jPanelGraficos.setLayout(new java.awt.GridLayout(1, 3, 5, 5));

// ======================================================
// GRÁFICO 1: UTILIZACIÓN CPU (El que ya tenías)
// ======================================================
this.pieDataset = new DefaultPieDataset();
this.pieDataset.setValue("CPU Ocioso", 100.0);
this.pieDataset.setValue("CPU en Uso", 0.0);

this.pieChart = ChartFactory.createPieChart(
    "Utilización de CPU", pieDataset, true, true, false
);
// IMPORTANTE: Usa la variable renombrada
this.pieChartPanel = new ChartPanel(pieChart); 
// Añadimos el primer gráfico
jPanelGraficos.add(this.pieChartPanel);

// ======================================================
// GRÁFICO 2: TIEMPOS PROMEDIO (Barras)
// ======================================================
this.timeDataset = new DefaultCategoryDataset();
this.timeDataset.setValue(0, "Ciclos", "Espera");
this.timeDataset.setValue(0, "Ciclos", "Retorno");
this.timeDataset.setValue(0, "Ciclos", "Respuesta");

this.timeBarChart = ChartFactory.createBarChart(
    "Tiempos Promedio",  // Título
    "Métrica",           // Eje X
    "Ciclos",            // Eje Y
    this.timeDataset,
    PlotOrientation.VERTICAL,
    false, true, false
);
this.timeChartPanel = new ChartPanel(this.timeBarChart);
// Añadimos el segundo gráfico
jPanelGraficos.add(this.timeChartPanel);

// ======================================================
// GRÁFICO 3: TAMAÑO DE COLAS (Barras)
// ======================================================
this.queueDataset = new DefaultCategoryDataset();
this.queueDataset.setValue(0, "Procesos", "Listos");
this.queueDataset.setValue(0, "Procesos", "Bloqueados");
this.queueDataset.setValue(0, "Procesos", "Terminados");

this.queueBarChart = ChartFactory.createBarChart(
    "Procesos en Cola", // Título
    "Colas",            // Eje X
    "Cantidad",         // Eje Y
    this.queueDataset,
    PlotOrientation.VERTICAL,
    false, true, false
);
this.queueChartPanel = new ChartPanel(this.queueBarChart);
// Añadimos el tercer gráfico
jPanelGraficos.add(this.queueChartPanel);

// ------------------------------------------------------
// Validamos el panel al final
jPanelGraficos.validate(); 
// --- FIN DE LA INICIALIZACIÓN DE GRÁFICOS ---

// (Aquí debe continuar tu código de la línea 85: txtPaneCPU.setContentType("text/html");)
    txtPaneCPU.setContentType("text/html");
    txtPaneCPU.setContentType("text/html");
    txtPaneCPU.setBackground(new java.awt.Color(240, 240, 240));
    txtPaneCPU.setText("<html><body style='font-family: Segoe UI; font-size: 10pt; color: #888888;'><i>Inactivo</i></body></html>");
    // --- 1. Configuración de JTextPane ---
    txtAreaReady.setContentType("text/html");
    
    txtAreaBlocked.setContentType("text/html");
    txtAreaTerminados.setContentType("text/html");


    for (Scheduler.SchedulingAlgorithm algo : Scheduler.SchedulingAlgorithm.values()) {
        cmbAlgoritmo.addItem(algo.toString()); 
    }

    // (Asegúrate de tener <ProcessType> en el JComboBox 'cmbTipoProceso')
    cmbTipoProceso.addItem(ProcessType.CPU_BOUND);
    cmbTipoProceso.addItem(ProcessType.IO_BOUND);

    // --- 3. ¡NUEVO! Inicializar la "Sala de Espera" ---
    this.procesosEnStaging = new Cola();

    // --- 4. Spinners (Valores por defecto Y tu código de tamaño) ---
    spinnerCicloMs.setValue(100);
    // ... (tus líneas de 'ancho' y 'alto' y 'setPreferredSize') ...
    spinnerQuantum.setValue(5);

    // --- 5. Código de Carga (¡Se queda!) ---
    // (Este try-catch carga el sim_config.csv, lo veremos en el Paso 7)
    try {
        // ... (el código de 'try-catch' para cargar 'sim_config.csv' va aquí) ...
        // ¡Lo programaremos en el Paso 7!
    } catch (Exception e) {
        // ...
    }

    // --- 6. Configuración inicial de botones (¡LA NUEVA LÓGICA!) ---
    btnIniciar.setEnabled(false); // Deshabilitado hasta que haya procesos
    btnPausar.setEnabled(false);
    btnDetener.setEnabled(false);
    btnAnadirProceso.setEnabled(true); // ¡SIEMPRE HABILITADO!
    // (Asumimos que btnCargarCSV y btnGuardarConfig están siempre habilitados)

    // --- 7. Listener del ComboBox (Tu código) ---
    // (El listener de 'cmbTipoProceso.addItemListener' va aquí)
    // ...

    // --- 8. Estado inicial de spinners (Tu código) ---
    spinnerCiclosExcep.setEnabled(false);
    spinnerCiclosIO.setEnabled(false);
    
    cmbTipoProceso.addItemListener(new java.awt.event.ItemListener() {
    public void itemStateChanged(java.awt.event.ItemEvent evt) {

        // Solo nos importa cuando algo es SELECCIONADO
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {

            // --- ¡El Arreglo que ya habíamos discutido! ---
            // 1. Obtenemos el item seleccionado Y LO CONVERTIMOS A STRING
            String itemSeleccionado = evt.getItem().toString();

            // 2. Comparamos STRING contra STRING
            boolean esIOBound = itemSeleccionado.equals(ProcessType.IO_BOUND.toString());
            // --- Fin del Arreglo ---

            // Habilitamos/deshabilitamos los spinners de E/S
            spinnerCiclosExcep.setEnabled(esIOBound);
            spinnerCiclosIO.setEnabled(esIOBound);

            // Si no es IO_BOUND, los reseteamos a 0
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

        // 1. Reloj
        lblRelojGlobal.setText(String.valueOf(so.getGlobalClock()));

        // 2. CPU
       PCB pcbEnCPU = so.getCpu().getCurrentProcess();

        if (pcbEnCPU != null) {
            
            lblRelojGlobal1.setText("Modo: Usuario");
            // (Asegúrate de importar java.lang.StringBuilder si da error)
            StringBuilder sb = new StringBuilder();
            
            // Estilos (puedes ajustar el 'font-size' si quieres)
            sb.append("<html><body style='font-family: Segoe UI; font-size: 10pt;'>");

            // Línea 1: Nombre e ID
            sb.append("<b>").append(pcbEnCPU.getName()).append("</b>");
            sb.append(" (ID: ").append(pcbEnCPU.getId()).append(")<br>"); // <br> es un salto de línea

            // Línea 2: Registros PC y MAR
            sb.append("PC: <b>").append(pcbEnCPU.getProgramCounter()).append("</b>");
            sb.append(" / MAR: <b>").append(pcbEnCPU.getMemoryAddressRegister()).append("</b><br>");

            // Línea 3: Ciclos restantes
            sb.append("Ciclos Restantes: <b>").append(pcbEnCPU.getCiclosRestantes()).append("</b>");

            // Cierre del HTML
            sb.append("</body></html>");
            
            // Enviamos el HTML al JTextPane
            txtPaneCPU.setText(sb.toString());

        } else {
            
            lblRelojGlobal1.setText("Modo: Kernel");
            // Si no hay proceso, mostramos "Inactivo" (con el mismo estilo)
            txtPaneCPU.setText("<html><body style='font-family: Segoe UI; font-size: 10pt; color: #888888;'><i>Inactivo</i></body></html>");
        }

        // 3. Colas (¡Gracias a tu método .toString() en la clase Cola!)
        txtAreaReady.setText(so.getReadyQueue().toString());
        txtAreaBlocked.setText(so.getBlockedQueue().toString());
        txtAreaTerminados.setText(so.getTerminatedQueue().toString());
        actualizarGraficoCPU();
        
        actualizarMetricasGUI();
        // (Opcional: actualiza el contador de terminados si lo dejaste)
        // lblTerminados.setText(String.valueOf(so.getTerminatedQueue().getSize()));
        actualizarGraficosBarras();
    }

    /**
     * Se llama cuando la simulación termina.
     */
 
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
        
        
        
        // ¡BORRA ESTA LÍNEA!
        // metricas.calcularMetricasFinales(); // <-- Esta línea se va.

        // (Importa java.text.DecimalFormat si quieres formatearlo bonito)
        // O usamos String.format que es más fácil:
        
        lblThroughput.setText(String.format("%.4f", metricas.calcularThroughput()));
        lblUtilizacionCPU.setText(String.format("%.2f %%", metricas.calcularUtilizacionCPU() * 100));
        lblTurnaround.setText(String.format("%.2f", metricas.calcularTiempoDeRetornoPromedio()));
        lblEspera.setText(String.format("%.2f", metricas.calcularTiempoDeEsperaPromedio()));
        lblRespuesta.setText(String.format("%.2f", metricas.calcularTiempoDeRespuestaPromedio()));

        // Habilitar botones para una nueva simulación
        btnIniciar.setEnabled(true);
        btnCargarCSV.setEnabled(true);
        btnPausar.setEnabled(false);
        btnDetener.setEnabled(false);
        
        javax.swing.JOptionPane.showMessageDialog(this, "¡Simulación Finalizada!");
    }

    /**
     * Actualiza los datos del gráfico de torta.
     * Este método debe ser llamado en cada "tick" de la simulación.
     */
    private void actualizarGraficoCPU() {
        // Asegurarnos de que el gestor de métricas exista
        if (so == null || so.getGestorMetricas() == null) {
            return;
        }
        
        

        // 1. Obtener la métrica de utilización de tu clase GestorDeMetricas
        // (Ajusta el nombre del método si es necesario!)
        double utilizacion = so.getGestorMetricas().calcularUtilizacionCPU(); 

        // 2. Calcular los porcentajes
        double porcentajeUso = utilizacion * 100.0;
        double porcentajeOcioso = (1.0 - utilizacion) * 100.0;

        // 3. Actualizar el dataset. 
        this.pieDataset.setValue("CPU en Uso", porcentajeUso);
        this.pieDataset.setValue("CPU Ocioso", porcentajeOcioso);
    }
    
    // --- PEGA ESTE MÉTODO NUEVO EN LA LÍNEA 272 ---

private void actualizarGraficosBarras() {
    if (so == null || so.getGestorMetricas() == null) {
        return;
    }
    
    // 1. Actualizar Gráfico de Tiempos (usa el GestorDeMetricas)
    GestorDeMetricas metricas = so.getGestorMetricas();
    this.timeDataset.setValue(metricas.calcularTiempoDeEsperaPromedio(), "Ciclos", "Espera");
    this.timeDataset.setValue(metricas.calcularTiempoDeRetornoPromedio(), "Ciclos", "Retorno");
    this.timeDataset.setValue(metricas.calcularTiempoDeRespuestaPromedio(), "Ciclos", "Respuesta");

    // 2. Actualizar Gráfico de Colas (usa el SistemaOperativo)
    // (Asegúrate de que tu clase 'Cola' tenga un método 'getSize()')
    this.queueDataset.setValue(so.getReadyQueue().getSize(), "Procesos", "Listos");
    this.queueDataset.setValue(so.getBlockedQueue().getSize(), "Procesos", "Bloqueados");
    this.queueDataset.setValue(so.getTerminatedQueue().getSize(), "Procesos", "Terminados");
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGuardarConfig)
                                .addGap(14, 14, 14)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnDetener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnIniciar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnPausar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                    .addComponent(btnDetener, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(280, Short.MAX_VALUE))
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

        btnCargarCSV.setText("Cargar CSV");
        btnCargarCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCargarCSVActionPerformed(evt);
            }
        });

        txtRutaCSV.setBackground(new java.awt.Color(204, 204, 204));
        txtRutaCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRutaCSVActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnCargarCSV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRutaCSV))
                .addGap(80, 80, 80))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCargarCSV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtRutaCSV, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

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
                .addContainerGap(47, Short.MAX_VALUE))
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
                        .addGap(0, 258, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanelGraficos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
        // --- 1. LEER LA CONFIGURACIÓN DE LA GUI ---
        String algoSeleccionado = (String) cmbAlgoritmo.getSelectedItem();
        Scheduler.SchedulingAlgorithm algoritmo = Scheduler.SchedulingAlgorithm.valueOf(algoSeleccionado);
        int quantum = (Integer) spinnerQuantum.getValue();
        int cicloMs = (Integer) spinnerCicloMs.getValue();

        // --- 2. ¡AQUÍ ESTÁ EL CÓDIGO FALTANTE! ---
        // (Crear todos los componentes que el S.O. necesita)
        // (Asegúrate de importar CPU, Scheduler, GestorEstados, etc.)
        Cola readyQueue = new Cola();
        Cola blockedQueue = new Cola();
        Cola suspendedReadyQueue = new Cola(); // Necesaria para GestorEstados
        
        Scheduler scheduler = new Scheduler(readyQueue, algoritmo, quantum);
        GestorEstados gestorEstados = new GestorEstados(readyQueue, blockedQueue, suspendedReadyQueue, scheduler);
        CPU cpu = new CPU(quantum); // La CPU también necesita el quantum
        // ------------------------------------------

        // --- 3. ¡Usar el ÚNICO constructor del S.O.! ---
        // (Ahora 'cpu', 'scheduler', etc., SÍ existen)
        this.so = new SistemaOperativo(cpu, scheduler, gestorEstados, readyQueue, blockedQueue);
        
        // --- 4. Cargar los procesos desde nuestra cola de Staging ---
        txtAreaReady.setText(""); // Limpiamos la lista visual
        for (int i = 0; i < procesosEnStaging.getSize(); i++) {
            
            // 1. Obtenemos el original (sin sacarlo)
            PCB pcbOriginal = procesosEnStaging.get(i);
            
            // 2. Creamos un CLON "fresco" (del Paso 1 de mi respuesta anterior)
            PCB pcbClonado = new PCB(pcbOriginal); 
            
            pcbClonado.setStatus(ProcessStatus.READY);
            so.getScheduler().reinsertProcess(pcbClonado); // Añadimos el clon
        }

        // --- 5. Configurar y arrancar el Timer ---
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
        
        // --- 6. Actualizar botones ---
        btnIniciar.setEnabled(false);
        btnAnadirProceso.setEnabled(true); // Sigue activo
        btnPausar.setEnabled(true);
        btnDetener.setEnabled(true);
        btnCargarCSV.setEnabled(false); // Deshabilitamos cargar/guardar
        btnGuardarConfig.setEnabled(false); // mientras corre
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error al iniciar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); 
    }
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnPausarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPausarActionPerformed
        // TODO add your handling code here:
        if (this.simulationTimer == null) {
        return;
    }

    // Comprobamos si el Timer (nuestro loop) está corriendo
    if (this.simulationTimer.isRunning()) {
        // --- SI ESTÁ CORRIENDO: LO PAUSAMOS ---
        this.simulationTimer.stop();

        // Cambiamos el texto del botón
        btnPausar.setText("Reanudar");

    } else {
        // --- SI ESTÁ PAUSADO: LO REANUDAMOS ---
        this.simulationTimer.start();

        // Devolvemos el texto original al botón
        btnPausar.setText("Pausar");
    }
    }//GEN-LAST:event_btnPausarActionPerformed

    private void btnCargarCSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCargarCSVActionPerformed
    // (Asegúrate de importar javax.swing.JFileChooser y java.io.File)
    JFileChooser fileChooser = new JFileChooser();
    int returnValue = fileChooser.showOpenDialog(this);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        txtRutaCSV.setText(selectedFile.getAbsolutePath());
        
        // (Importa java.io.BufferedReader, java.io.FileReader, java.io.FileNotFoundException)
        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            
            // --- 1. LEER LA CONFIGURACIÓN (Línea 1) ---
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
            
            // --- 2. LEER LOS PROCESOS (Líneas 2 en adelante) ---
            procesosEnStaging.limpiarCola(); // Limpiamos la "sala de espera" actual
            String processLine;
            int procesosCargados = 0;
            
            // (Importa proyecto1_franco_barra_roger_balan.PCB)
            // (Importa proyecto1_franco_barra_roger_balan.ProcessType)
            while ((processLine = reader.readLine()) != null) {
                String[] pcbParts = processLine.split(",");
                if (pcbParts.length < 5) continue; // Ignorar líneas mal formadas
                
                // Creamos el PCB (con arrivalTime 0 por defecto)
                PCB newPcb = new PCB(
                        pcbParts[0],                                 // Nombre
                        Integer.parseInt(pcbParts[1]),             // Instrucciones
                        ProcessType.valueOf(pcbParts[2]),            // Tipo
                        Integer.parseInt(pcbParts[3]),             // CiclosExcep
                        Integer.parseInt(pcbParts[4]),             // CiclosIO
                        0                                          // ArrivalTime
                );
                
                newPcb.setStatus(ProcessStatus.NEW);
                procesosEnStaging.agregar(newPcb);
                procesosCargados++;
            }
            
            // --- 3. Actualizar GUI y botones ---
            if (procesosCargados > 0) {
                btnIniciar.setEnabled(true);
            }
            txtAreaReady.setText(procesosEnStaging.toString()); // Mostramos en la "sala de espera"
            
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

    // --- 3. Limpiar colas (visuales Y lógicas) ---
    lblRelojGlobal.setText("0");
    txtAreaReady.setText("");
    txtAreaBlocked.setText("");
    txtAreaTerminados.setText("");
    // ... (limpiar métricas) ...


    // --- 4. Resetear botones ---
    btnIniciar.setEnabled(false); // No se puede iniciar sin procesos
    btnAnadirProceso.setEnabled(true);
    btnPausar.setEnabled(false);
    btnDetener.setEnabled(false);
    btnCargarCSV.setEnabled(true); // Habilitamos cargar/guardar
    btnGuardarConfig.setEnabled(true);

    // --- 5. Destruir simulación ---
    this.so = null;
    this.simulationTimer = null; 

    JOptionPane.showMessageDialog(this, "Simulación detenida.");
    }//GEN-LAST:event_btnDetenerActionPerformed

    private void btnGuardarConfigActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarConfigActionPerformed
     // (Asegúrate de importar java.io.FileWriter y java.io.IOException)
    try (java.io.FileWriter writer = new java.io.FileWriter("sim_config.csv")) {
        
        // --- 1. GUARDAR LA CONFIGURACIÓN (Línea 1) ---
        String algoritmo = (String) cmbAlgoritmo.getSelectedItem();
        int cicloMs = (Integer) spinnerCicloMs.getValue();
        int quantum = (Integer) spinnerQuantum.getValue();
        
        // Escribimos la línea de configuración
        writer.write(algoritmo + "," + cicloMs + "," + quantum + "\n");

        // --- 2. GUARDAR LOS PROCESOS DE LA "SALA DE ESPERA" (Líneas 2 en adelante) ---
        // (Usamos el 'for' loop que creamos, ¡sin java.util!)
        for (int i = 0; i < procesosEnStaging.getSize(); i++) {
            PCB pcb = procesosEnStaging.get(i);
            
            // Creamos una línea CSV para el PCB
            // Formato: Nombre,Instrucciones,Tipo,CiclosExcep,CiclosIO
            String pcbLine = String.format("%s,%d,%s,%d,%d\n",
                    pcb.getName(),
                    pcb.getLongitudPrograma(),
                    pcb.getType().toString(),
                    pcb.getCiclosParaExcepcion(),
                    pcb.getCiclosParaSatisfacerIO()
            );
            writer.write(pcbLine); // Escribimos la línea del proceso
        }
        
        // --- 3. Mensaje de éxito ---
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
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTipoProcesoActionPerformed

    private void btnAnadirProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAnadirProcesoActionPerformed
        try {
        // 1. Obtener valores del formulario
        String nombre = txtNombreProceso.getText();
        int instrucciones = (Integer) spinnerInstrucciones.getValue();
        ProcessType tipo = (ProcessType) cmbTipoProceso.getSelectedItem();
        int ciclosExcep = (Integer) spinnerCiclosExcep.getValue();
        int ciclosIO = (Integer) spinnerCiclosIO.getValue();

        // --- 2. VALIDACIÓN DE NOMBRE (¡Tu requisito!) ---
        if (nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validar en Staging
        if (procesosEnStaging.buscarPorNombre(nombre)) {
            JOptionPane.showMessageDialog(this, "Error: Ya existe un proceso con ese nombre en la lista de espera.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Validar en S.O. (si ya está corriendo)
        if (so != null && (so.getReadyQueue().buscarPorNombre(nombre) || 
                            so.getBlockedQueue().buscarPorNombre(nombre) ||
                            so.getTerminatedQueue().buscarPorNombre(nombre))) {
            JOptionPane.showMessageDialog(this, "Error: Ya existe un proceso con ese nombre en la simulación.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ... (Otras validaciones, como la de I/O) ...

        // 3. Crear el PCB
        long arrivalTime = (so != null) ? so.getGlobalClock() : 0;
        PCB newPcb = new PCB(nombre, instrucciones, tipo, ciclosExcep, ciclosIO, arrivalTime);

        // --- 4. LÓGICA DE AÑADIR (MODIFICADA) ---
        if (so == null || !simulationTimer.isRunning()) {
            // A: Simulación NO ha empezado. Añadir a Staging.
            newPcb.setStatus(ProcessStatus.NEW);
            procesosEnStaging.agregar(newPcb);
            btnIniciar.setEnabled(true); // ¡HABILITAMOS "INICIAR"!

            // (Truco visual: usa la cola de terminados para ver la lista de Staging)
            txtAreaReady.setText(procesosEnStaging.toString());

        } else {
            // B: Simulación SÍ está corriendo. Añadir directo al Scheduler.
            newPcb.setStatus(ProcessStatus.READY);
            so.getScheduler().reinsertProcess(newPcb);
        }

        // 5. Limpiar el formulario
        txtNombreProceso.setText("");
        spinnerInstrucciones.setValue(50); // (o el default que quieras)

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error creando proceso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }//GEN-LAST:event_btnAnadirProcesoActionPerformed

    private void txtNombreProcesoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNombreProcesoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNombreProcesoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
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


        /* Create and display the form */
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
