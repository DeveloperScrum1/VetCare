package vetcare.gui;

import vetcare.db.DuenoDB;
import vetcare.modelo.Dueno;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelDuenos extends JPanel {

    private DefaultTableModel modelo;
    private JTable            tabla;
    private JTextField        txtBuscar;

    private static final String[] COLUMNAS = {"ID", "Nombre", "DNI", "Teléfono", "Correo", "Dirección"};

    public PanelDuenos() {
        setLayout(new BorderLayout(0, 0));
        setBackground(MainFrame.FONDO);
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
        // Barra superior
        JPanel barraTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barraTop.setBackground(MainFrame.VERDE_CLAR);
        JLabel lblBus = new JLabel("Buscar por nombre o DNI:");
        lblBus.setFont(new Font("Calibri", Font.BOLD, 12));
        lblBus.setForeground(MainFrame.VERDE);
        txtBuscar = new JTextField(22);
        JButton btnBus = boton("Buscar", MainFrame.VERDE_MED);
        JButton btnRef = boton("Actualizar tabla", MainFrame.VERDE);
        btnBus.addActionListener(e -> buscar());
        btnRef.addActionListener(e -> cargarTabla());
        barraTop.add(lblBus); barraTop.add(txtBuscar);
        barraTop.add(btnBus); barraTop.add(btnRef);

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
        tabla.setGridColor(new Color(0xCC, 0xCC, 0xCC));
        tabla.setColumnSelectionAllowed(false);
        tabla.getColumnModel().getColumn(0).setMaxWidth(45);

        JScrollPane scroll = new JScrollPane(tabla);

        // Botones inferiores
        JPanel barraBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        barraBot.setBackground(MainFrame.VERDE_CLAR);
        JButton btnNuevo    = boton("+ Registrar dueño",   MainFrame.VERDE);
        JButton btnMod      = boton("Modificar selección", MainFrame.AMBAR);
        JButton btnEliminar = boton("Eliminar selección",  new Color(0xC0, 0x39, 0x2B));
        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnMod.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        barraBot.add(btnNuevo); barraBot.add(btnMod); barraBot.add(btnEliminar);

        add(barraTop, BorderLayout.NORTH);
        add(scroll,   BorderLayout.CENTER);
        add(barraBot, BorderLayout.SOUTH);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            List<Dueno> lista = DuenoDB.listar();
            for (Dueno d : lista)
                modelo.addRow(new Object[]{
                    d.getId(), d.getNombre(), d.getDni(),
                    d.getTelefono(), d.getCorreo(), d.getDireccion()
                });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar dueños: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        modelo.setRowCount(0);
        try {
            for (Dueno d : DuenoDB.listar()) {
                if (d.getNombre().toLowerCase().contains(texto) ||
                    d.getDni().toLowerCase().contains(texto))
                    modelo.addRow(new Object[]{
                        d.getId(), d.getNombre(), d.getDni(),
                        d.getTelefono(), d.getCorreo(), d.getDireccion()
                    });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** RF-17: abre el formulario con los datos del dueño seleccionado. */
    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un dueño."); return; }
        Object[] datos = new Object[6];
        for (int i = 0; i < 6; i++) datos[i] = modelo.getValueAt(fila, i);
        abrirFormulario(datos);
    }

    private void abrirFormulario(Object[] existente) {
        JTextField fNombre   = new JTextField(20);
        JTextField fDni      = new JTextField(20);
        JTextField fTelefono = new JTextField(20);
        JTextField fCorreo   = new JTextField(20);
        JTextField fDir      = new JTextField(20);

        if (existente != null) {
            fNombre.setText(String.valueOf(existente[1]));
            fDni.setText(String.valueOf(existente[2]));
            fDni.setEnabled(false);
            fTelefono.setText(existente[3] == null ? "" : String.valueOf(existente[3]));
            fCorreo.setText(existente[4] == null ? "" : String.valueOf(existente[4]));
            fDir.setText(existente[5] == null ? "" : String.valueOf(existente[5]));
        }

        Object[] campos = {
            "Nombre completo:", fNombre,
            "DNI:",             fDni,
            "Teléfono:",        fTelefono,
            "Correo:",          fCorreo,
            "Dirección:",       fDir
        };
        int r = JOptionPane.showConfirmDialog(this, campos, existente == null ? "Registrar Dueño" : "Modificar Dueño",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (r != JOptionPane.OK_OPTION) return;

        String nombre = fNombre.getText().trim();
        String dni    = fDni.getText().trim();
        if (nombre.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y DNI son obligatorios.");
            return;
        }
        if (existente != null) {
            try {
                DuenoDB.actualizar((int) existente[0], nombre, fTelefono.getText().trim(), fCorreo.getText().trim(), fDir.getText().trim());
                cargarTabla();
                JOptionPane.showMessageDialog(this, "Dueño modificado correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }
        try {
            if (DuenoDB.existeDni(dni)) {
                JOptionPane.showMessageDialog(this, "Ya existe un dueño con ese DNI.");
                return;
            }
            Dueno d = new Dueno(0, nombre, dni,
                fTelefono.getText().trim(), fCorreo.getText().trim(), fDir.getText().trim());
            DuenoDB.insertar(d);
            cargarTabla();
            JOptionPane.showMessageDialog(this, "Dueño registrado correctamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona un dueño."); return; }
        int id     = (int) modelo.getValueAt(fila, 0);
        String nom = (String) modelo.getValueAt(fila, 1);
        int conf = JOptionPane.showConfirmDialog(this,
            "Eliminar a " + nom + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            DuenoDB.eliminar(id);
            cargarTabla();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton boton(String texto, Color fondo) {
        JButton b = new JButton(texto);
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Calibri", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
