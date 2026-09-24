package vetcare.gui;

import vetcare.db.VeterinarioDB;
import vetcare.modelo.Veterinario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelVeterinarios extends JPanel {

    private DefaultTableModel modelo;
    private JTable            tabla;

    private static final String[] COLUMNAS =
        {"ID", "Nombre", "DNI", "Especialidad", "Colegiatura", "Teléfono", "Activo"};

    public PanelVeterinarios() {
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
        tabla.getColumnModel().getColumn(6).setMaxWidth(60);

        JScrollPane scroll = new JScrollPane(tabla);

        JPanel barraBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        barraBot.setBackground(MainFrame.VERDE_CLAR);
        JButton btnNuevo     = boton("+ Registrar veterinario", MainFrame.VERDE);
        JButton btnMod       = boton("Modificar selección",      MainFrame.VERDE_MED);
        JButton btnDesact    = boton("Desactivar selección",     MainFrame.AMBAR);
        JButton btnEliminar  = boton("Eliminar selección",       new Color(0xC0, 0x39, 0x2B));
        JButton btnActualizar = boton("Actualizar tabla",         MainFrame.VERDE_MED);
        btnNuevo.addActionListener(e    -> abrirFormulario(null));
        btnMod.addActionListener(e      -> modificar());
        btnDesact.addActionListener(e   -> desactivar());
        btnEliminar.addActionListener(e -> eliminar());
        btnActualizar.addActionListener(e -> cargarTabla());
        barraBot.add(btnNuevo); barraBot.add(btnMod); barraBot.add(btnDesact);
        barraBot.add(btnEliminar); barraBot.add(btnActualizar);

        add(scroll,   BorderLayout.CENTER);
        add(barraBot, BorderLayout.SOUTH);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            for (Veterinario v : VeterinarioDB.listar())
                modelo.addRow(new Object[]{
                    v.getId(), v.getNombre(), v.getDni(), v.getEspecialidad(),
                    v.getColegiatura(), v.getTelefono(), v.isActivo() ? "Si" : "No"
                });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** RF-33: abre el formulario con los datos del veterinario seleccionado. */
    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un veterinario."); return; }
        try {
            Veterinario v = VeterinarioDB.buscarPorId((int) modelo.getValueAt(fila, 0));
            if (v != null) abrirFormulario(v);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormulario(Veterinario existente) {
        JTextField fNombre  = new JTextField(20);
        JTextField fDni     = new JTextField(20);
        JTextField fTel     = new JTextField(20);
        JTextField fCorreo  = new JTextField(20);
        JTextField fEsp     = new JTextField(20);
        JTextField fCol     = new JTextField(20);

        if (existente != null) {
            fNombre.setText(existente.getNombre()); fDni.setText(existente.getDni());
            fTel.setText(existente.getTelefono() == null ? "" : existente.getTelefono());
            fCorreo.setText(existente.getCorreo() == null ? "" : existente.getCorreo());
            fEsp.setText(existente.getEspecialidad()); fCol.setText(existente.getColegiatura());
        }

        Object[] campos = {
            "Nombre:", fNombre, "DNI:", fDni,
            "Teléfono:", fTel, "Correo:", fCorreo,
            "Especialidad:", fEsp, "N.° colegiatura:", fCol
        };
        int r = JOptionPane.showConfirmDialog(this, campos, existente == null ? "Registrar Veterinario" : "Modificar Veterinario",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String nombre = fNombre.getText().trim();
        String dni    = fDni.getText().trim();
        if (nombre.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y DNI son obligatorios.");
            return;
        }
        try {
            if (existente == null) {
                Veterinario v = new Veterinario(0, nombre, dni, fTel.getText().trim(),
                    fCorreo.getText().trim(), fEsp.getText().trim(), fCol.getText().trim());
                VeterinarioDB.insertar(v);
            } else {
                VeterinarioDB.actualizar(existente.getId(), nombre, dni, fTel.getText().trim(),
                    fCorreo.getText().trim(), fEsp.getText().trim(), fCol.getText().trim());
            }
            cargarTabla();
            JOptionPane.showMessageDialog(this, existente == null ? "Veterinario registrado correctamente." : "Veterinario modificado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void desactivar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un veterinario."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        try {
            VeterinarioDB.actualizarActivo(id, false);
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un veterinario."); return; }
        int id = (int) modelo.getValueAt(fila, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Eliminar veterinario?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            VeterinarioDB.eliminar(id);
            cargarTabla();
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
}
