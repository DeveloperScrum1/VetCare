package vetcare.gui;

import vetcare.db.ConsultaDB;
import vetcare.db.MascotaDB;
import vetcare.modelo.ConsultaMedica;
import vetcare.modelo.Mascota;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelHistorial extends JPanel {

    private DefaultTableModel modelo;
    private JTable            tabla;
    private JComboBox<String> cbMascota;
    private List<Mascota>     mascotas;

    private static final String[] COLUMNAS =
        {"ID", "Fecha", "Diagnóstico", "Tratamiento", "Observaciones"};

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PanelHistorial() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.FONDO);
        construirUI();
        cargarMascotas();
    }

    private void construirUI() {
        // Barra superior: selector de mascota
        JPanel barraTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barraTop.setBackground(MainFrame.VERDE_CLAR);
        JLabel lbl = new JLabel("Mascota:");
        lbl.setFont(new Font("Calibri", Font.BOLD, 12));
        lbl.setForeground(MainFrame.VERDE);
        cbMascota = new JComboBox<>();
        cbMascota.setFont(new Font("Calibri", Font.PLAIN, 12));
        cbMascota.setPreferredSize(new Dimension(260, 26));
        JButton btnVer = boton("Ver historial", MainFrame.VERDE);
        JButton btnRef = boton("Actualizar lista", MainFrame.VERDE_MED);
        btnVer.addActionListener(e -> cargarHistorial());
        btnRef.addActionListener(e -> cargarMascotas());
        JButton btnRep = boton("Reporte por período", new Color(0x2C, 0x3E, 0x50));
        btnRep.addActionListener(e -> reportePeriodo());
        barraTop.add(lbl); barraTop.add(cbMascota); barraTop.add(btnVer); barraTop.add(btnRef); barraTop.add(btnRep);

        // Tabla
        modelo = new DefaultTableModel(COLUMNAS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        tabla.setFont(new Font("Calibri", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("Calibri", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(MainFrame.VERDE);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(0xD5, 0xF5, 0xE3));
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(220);

        JScrollPane scroll = new JScrollPane(tabla);

        // Barra inferior: registrar consulta + eliminar
        JPanel barraBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        barraBot.setBackground(MainFrame.VERDE_CLAR);
        JButton btnNueva = boton("+ Registrar consulta",  MainFrame.VERDE);
        JButton btnElim  = boton("Eliminar selección",     new Color(0xC0, 0x39, 0x2B));
        btnNueva.addActionListener(e -> registrarConsulta());
        btnElim.addActionListener(e  -> eliminarConsulta());
        barraBot.add(btnNueva); barraBot.add(btnElim);

        add(barraTop, BorderLayout.NORTH);
        add(scroll,   BorderLayout.CENTER);
        add(barraBot, BorderLayout.SOUTH);
    }

    private void cargarMascotas() {
        cbMascota.removeAllItems();
        try {
            mascotas = MascotaDB.listar();
            mascotas.forEach(m -> cbMascota.addItem(m.getId() + " — " + m.getAnimal().getNombre()
                + " (" + m.getAnimal().getEspecie() + ")"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarHistorial() {
        modelo.setRowCount(0);
        if (cbMascota.getSelectedIndex() < 0 || mascotas == null || mascotas.isEmpty()) return;
        int idMascota = mascotas.get(cbMascota.getSelectedIndex()).getId();
        try {
            for (ConsultaMedica c : ConsultaDB.listarPorMascota(idMascota))
                modelo.addRow(new Object[]{
                    c.getId(), c.getFecha(), c.getDiagnostico(),
                    c.getTratamiento(), c.getObservaciones()
                });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarConsulta() {
        if (cbMascota.getSelectedIndex() < 0 || mascotas == null || mascotas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona primero una mascota.");
            return;
        }
        int idMascota = mascotas.get(cbMascota.getSelectedIndex()).getId();

        JTextField fFecha   = new JTextField(LocalDate.now().format(FORMATO_FECHA), 12);
        JTextField fDiag    = new JTextField(30);
        JTextField fTrat    = new JTextField(30);
        JTextField fObs     = new JTextField(30);

        Object[] campos = {
            "Fecha (dd/MM/yyyy):", fFecha,
            "Diagnóstico:",        fDiag,
            "Tratamiento:",        fTrat,
            "Observaciones:",      fObs
        };
        int r = JOptionPane.showConfirmDialog(this, campos, "Registrar Consulta Médica",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String diag = fDiag.getText().trim();
        if (diag.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El diagnóstico es obligatorio.");
            return;
        }
        try {
            ConsultaDB.insertar(idMascota, fFecha.getText().trim(), diag,
                fTrat.getText().trim(), fObs.getText().trim());
            cargarHistorial();
            JOptionPane.showMessageDialog(this, "Consulta registrada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarConsulta() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una consulta."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Eliminar consulta #" + id + "?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            ConsultaDB.eliminar(id);
            cargarHistorial();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo); b.setForeground(Color.WHITE);
        b.setFont(new Font("Calibri", Font.BOLD, 12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** RF-40: reporte de las consultas realizadas entre dos fechas, con total y exportación a CSV. */
    private void reportePeriodo() {
        JSpinner spDesde = new JSpinner(new SpinnerDateModel());
        spDesde.setEditor(new JSpinner.DateEditor(spDesde, "dd/MM/yyyy"));
        JSpinner spHasta = new JSpinner(new SpinnerDateModel());
        spHasta.setEditor(new JSpinner.DateEditor(spHasta, "dd/MM/yyyy"));
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        spDesde.setValue(cal.getTime());
        Object[] campos = { "Desde:", spDesde, "Hasta:", spHasta };
        int r = JOptionPane.showConfirmDialog(this, campos, "Reporte de consultas por período",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        LocalDate desde = ((java.util.Date) spDesde.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate hasta = ((java.util.Date) spHasta.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        if (hasta.isBefore(desde)) {
            JOptionPane.showMessageDialog(this, "La fecha final no puede ser anterior a la inicial.");
            return;
        }
        List<Object[]> filas;
        try {
            filas = ConsultaDB.listarPorPeriodo(desde, hasta);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String[] cols = {"Fecha", "Mascota", "Diagnóstico", "Tratamiento", "Observaciones"};
        DefaultTableModel m = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        filas.forEach(m::addRow);
        JTable t = new JTable(m);
        t.setRowHeight(22);
        t.getTableHeader().setBackground(MainFrame.VERDE);
        t.getTableHeader().setForeground(Color.WHITE);
        JScrollPane sp = new JScrollPane(t);
        sp.setPreferredSize(new Dimension(780, 320));
        JLabel resumen = new JLabel("Período: " + desde.format(FORMATO_FECHA) + " al " + hasta.format(FORMATO_FECHA)
            + "   |   Total de consultas: " + filas.size());
        resumen.setFont(new Font("Calibri", Font.BOLD, 13));
        JButton btnCsv = boton("Exportar a CSV", MainFrame.VERDE_MED);
        btnCsv.addActionListener(e -> exportarCsv(cols, filas));
        JPanel pie = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pie.add(btnCsv);
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.add(resumen, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        panel.add(pie, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, panel, "Reporte de consultas", JOptionPane.PLAIN_MESSAGE);
    }

    private void exportarCsv(String[] cols, List<Object[]> filas) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("reporte_consultas.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (java.io.BufferedWriter w = java.nio.file.Files.newBufferedWriter(
                fc.getSelectedFile().toPath(), java.nio.charset.StandardCharsets.UTF_8)) {
            w.write(String.join(";", cols));
            w.newLine();
            for (Object[] f : filas) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < f.length; i++) {
                    if (i > 0) sb.append(';');
                    String v = f[i] == null ? "" : String.valueOf(f[i]);
                    sb.append('"').append(v.replace("\"", "\"\"")).append('"');
                }
                w.write(sb.toString());
                w.newLine();
            }
            JOptionPane.showMessageDialog(this, "Reporte exportado a " + fc.getSelectedFile().getAbsolutePath());
        } catch (java.io.IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
