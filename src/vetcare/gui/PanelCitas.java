package vetcare.gui;

import vetcare.db.CitaDB;
import vetcare.db.MascotaDB;
import vetcare.db.VeterinarioDB;
import vetcare.modelo.Mascota;
import vetcare.modelo.Veterinario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PanelCitas extends JPanel {

    private DefaultTableModel modelo;
    private JTable            tabla;
    private JTextField        txtBuscar;

    private static final String[] COLUMNAS =
        {"ID", "Mascota", "Veterinario", "Fecha", "Hora", "Motivo", "Estado", "Cancelación"};

    public PanelCitas() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.FONDO);
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
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

        JScrollPane scroll = new JScrollPane(tabla);

        JPanel barraBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        barraBot.setBackground(MainFrame.VERDE_CLAR);
        JButton btnNuevo    = boton("+ Agendar cita",        MainFrame.VERDE);
        JButton btnMod      = boton("Modificar cita",         MainFrame.VERDE_MED);
        JButton btnAtend    = boton("Marcar Atendida",        MainFrame.VERDE_MED);
        JButton btnNoAsis   = boton("Marcar No asistió",      MainFrame.AMBAR);
        JButton btnCancelar = boton("Cancelar cita",          new Color(0xC0, 0x39, 0x2B));
        JButton btnEliminar = boton("Eliminar cita",          new Color(0x7B, 0x24, 0x1F));
        JButton btnRef      = boton("Actualizar tabla",        new Color(0x2C, 0x3E, 0x50));

        btnNuevo.addActionListener(e    -> abrirFormulario());
        btnMod.addActionListener(e      -> modificar());
        btnAtend.addActionListener(e    -> actualizarEstado("ATENDIDA", null));
        btnNoAsis.addActionListener(e   -> actualizarEstado("NO_ASISTIO", null));
        btnCancelar.addActionListener(e -> cancelar());
        btnEliminar.addActionListener(e -> eliminar());
        btnRef.addActionListener(e      -> cargarTabla());

        barraBot.add(btnNuevo); barraBot.add(btnMod); barraBot.add(btnAtend); barraBot.add(btnNoAsis);
        barraBot.add(btnCancelar); barraBot.add(btnEliminar); barraBot.add(btnRef);

        JPanel barraTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        JLabel lblB = new JLabel("Buscar citas por mascota:");
        lblB.setFont(new Font("Calibri", Font.BOLD, 12));
        lblB.setForeground(MainFrame.VERDE);
        txtBuscar = new JTextField(18);
        JButton btnBuscar = boton("Buscar", MainFrame.VERDE_MED);
        JButton btnTodas  = boton("Ver todas", MainFrame.VERDE);
        btnBuscar.addActionListener(e -> buscarPorMascota());
        txtBuscar.addActionListener(e -> buscarPorMascota());
        btnTodas.addActionListener(e -> { txtBuscar.setText(""); cargarTabla(); });
        barraTop.add(lblB); barraTop.add(txtBuscar); barraTop.add(btnBuscar); barraTop.add(btnTodas);

        add(barraTop, BorderLayout.NORTH);
        add(scroll,   BorderLayout.CENTER);
        add(barraBot, BorderLayout.SOUTH);
    }

    /** RF-27: muestra solo las citas de las mascotas cuyo nombre contiene el texto. */
    private void buscarPorMascota() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) { cargarTabla(); return; }
        modelo.setRowCount(0);
        try {
            for (Object[] fila : CitaDB.listarFilasPorMascota(texto))
                modelo.addRow(fila);
            if (modelo.getRowCount() == 0)
                JOptionPane.showMessageDialog(this, "No hay citas para una mascota con ese nombre.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** RF-24: cambia veterinario, fecha, hora o motivo de una cita pendiente, evitando cruces de horario. */
    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una cita."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        if (!"PENDIENTE".equals(modelo.getValueAt(fila, 6))) {
            JOptionPane.showMessageDialog(this, "Solo se pueden modificar citas PENDIENTES.");
            return;
        }
        List<Veterinario> vets;
        try {
            vets = VeterinarioDB.listar().stream().filter(Veterinario::isActivo).collect(java.util.stream.Collectors.toList());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (vets.isEmpty()) { JOptionPane.showMessageDialog(this, "No hay veterinarios activos."); return; }

        String vetActual = String.valueOf(modelo.getValueAt(fila, 2));
        JComboBox<String> cbVet = new JComboBox<>();
        int seleccion = 0;
        for (int i = 0; i < vets.size(); i++) {
            cbVet.addItem(vets.get(i).getId() + " - Dr/a. " + vets.get(i).getNombre());
            if (vets.get(i).getNombre().equals(vetActual)) seleccion = i;
        }
        cbVet.setSelectedIndex(seleccion);

        JSpinner spFecha = new JSpinner(new SpinnerDateModel());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        JSpinner spHora = new JSpinner(new SpinnerDateModel());
        spHora.setEditor(new JSpinner.DateEditor(spHora, "HH:mm"));
        try {
            spFecha.setValue(new SimpleDateFormat("dd/MM/yyyy").parse(String.valueOf(modelo.getValueAt(fila, 3))));
            spHora.setValue(new SimpleDateFormat("HH:mm").parse(String.valueOf(modelo.getValueAt(fila, 4))));
        } catch (java.text.ParseException ignorado) { }
        Object motivoActual = modelo.getValueAt(fila, 5);
        JTextField fMotivo = new JTextField(motivoActual == null ? "" : String.valueOf(motivoActual), 25);

        Object[] campos = {
            "Mascota: " + modelo.getValueAt(fila, 1),
            "Veterinario:", cbVet, "Fecha:", spFecha, "Hora:", spHora, "Motivo:", fMotivo
        };
        int r = JOptionPane.showConfirmDialog(this, campos, "Modificar Cita #" + id,
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        try {
            int idVet = vets.get(cbVet.getSelectedIndex()).getId();
            String fecha = new SimpleDateFormat("dd/MM/yyyy").format((Date) spFecha.getValue());
            String hora  = new SimpleDateFormat("HH:mm").format((Date) spHora.getValue());
            if (CitaDB.hayConflicto(fecha, hora, idVet, id)) {
                JOptionPane.showMessageDialog(this,
                    "El veterinario ya tiene una cita en ese horario.", "Conflicto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CitaDB.actualizar(id, idVet, fecha, hora, fMotivo.getText().trim());
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Cita modificada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            for (Object[] fila : CitaDB.listarFilas())
                modelo.addRow(fila);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormulario() {
        // Cargar mascotas y veterinarios
        DefaultComboBoxModel<String> mMascotas  = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<String> mVets      = new DefaultComboBoxModel<>();
        List<Mascota>      mascotas;
        List<Veterinario>  veterinarios;
        try {
            mascotas     = MascotaDB.listar();
            veterinarios = VeterinarioDB.listar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage()); return;
        }
        if (mascotas.isEmpty())     { JOptionPane.showMessageDialog(this, "No hay mascotas registradas."); return; }
        if (veterinarios.isEmpty()) { JOptionPane.showMessageDialog(this, "No hay veterinarios registrados."); return; }

        mascotas.forEach(m -> mMascotas.addElement(m.getId() + " - " + m.getAnimal().getNombre()));
        veterinarios.stream().filter(Veterinario::isActivo)
            .forEach(v -> mVets.addElement(v.getId() + " - Dr/a. " + v.getNombre()));

        JComboBox<String> cbMasc = new JComboBox<>(mMascotas);
        JComboBox<String> cbVet  = new JComboBox<>(mVets);

        JSpinner spFecha = new JSpinner(new SpinnerDateModel());
        spFecha.setEditor(new JSpinner.DateEditor(spFecha, "dd/MM/yyyy"));
        JSpinner spHora = new JSpinner(new SpinnerDateModel());
        spHora.setEditor(new JSpinner.DateEditor(spHora, "HH:mm"));

        JTextField fMotivo = new JTextField(25);

        Object[] campos = {
            "Mascota:", cbMasc,
            "Veterinario:", cbVet,
            "Fecha:", spFecha,
            "Hora:", spHora,
            "Motivo:", fMotivo
        };
        int r = JOptionPane.showConfirmDialog(this, campos, "Agendar Cita",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        try {
            int idMasc = mascotas.get(cbMasc.getSelectedIndex()).getId();
            int idVet  = veterinarios.stream().filter(Veterinario::isActivo)
                .collect(java.util.stream.Collectors.toList())
                .get(cbVet.getSelectedIndex()).getId();

            String fecha  = new SimpleDateFormat("dd/MM/yyyy").format((Date) spFecha.getValue());
            String hora   = new SimpleDateFormat("HH:mm").format((Date) spHora.getValue());
            String motivo = fMotivo.getText().trim();

            if (CitaDB.hayConflicto(fecha, hora, idVet)) {
                JOptionPane.showMessageDialog(this,
                    "El veterinario ya tiene una cita en ese horario.", "Conflicto", JOptionPane.WARNING_MESSAGE);
                return;
            }
            CitaDB.insertar(idMasc, idVet, fecha, hora, motivo);
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Cita agendada correctamente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstado(String estado, String motivoCancelacion) {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una cita."); return; }
        int    id          = (int) modelo.getValueAt(fila, 0);
        String estadoActual = (String) modelo.getValueAt(fila, 6);
        if (!"PENDIENTE".equals(estadoActual)) {
            JOptionPane.showMessageDialog(this, "Solo se pueden cambiar citas PENDIENTES.");
            return;
        }
        try {
            CitaDB.actualizarEstado(id, estado, motivoCancelacion);
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una cita."); return; }
        String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:");
        if (motivo == null) return;
        actualizarEstado("CANCELADA", motivo);
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una cita."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        int conf = JOptionPane.showConfirmDialog(this,
            "Esto borra la cita #" + id + " por completo (no solo la cancela). ¿Continuar?",
            "Eliminar cita", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            CitaDB.eliminar(id);
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
}
