package vetcare.gui;

import vetcare.db.DuenoDB;
import vetcare.db.MascotaDB;
import vetcare.modelo.Mascota;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelMascotas extends JPanel {

    private DefaultTableModel modelo;
    private JTable            tabla;
    private JTextField        txtBuscar;

    private static final String[] COLUMNAS =
        {"ID", "Especie", "Nombre", "Raza", "Edad", "Sexo", "Peso(kg)", "Dueño", "Observaciones"};

    public PanelMascotas() {
        setLayout(new BorderLayout());
        setBackground(MainFrame.FONDO);
        construirUI();
        cargarTabla();
    }

    private void construirUI() {
        JPanel barraTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        barraTop.setBackground(MainFrame.VERDE_CLAR);
        JLabel lbl = new JLabel("Buscar por nombre:");
        lbl.setFont(new Font("Calibri", Font.BOLD, 12));
        lbl.setForeground(MainFrame.VERDE);
        txtBuscar = new JTextField(18);
        JButton btnBus = boton("Buscar", MainFrame.VERDE_MED);
        JButton btnRef = boton("Actualizar", MainFrame.VERDE);
        btnBus.addActionListener(e -> buscar());
        btnRef.addActionListener(e -> cargarTabla());
        barraTop.add(lbl); barraTop.add(txtBuscar); barraTop.add(btnBus); barraTop.add(btnRef);

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

        JPanel barraBot = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        barraBot.setBackground(MainFrame.VERDE_CLAR);
        JButton btnNuevo   = boton("+ Registrar mascota",  MainFrame.VERDE);
        JButton btnMod     = boton("Modificar selección",    MainFrame.AMBAR);
        JButton btnElim    = boton("Eliminar selección",    new Color(0xC0, 0x39, 0x2B));
        btnNuevo.addActionListener(e -> abrirFormulario(null));
        btnMod.addActionListener(e   -> modificar());
        btnElim.addActionListener(e  -> eliminar());
        barraBot.add(btnNuevo); barraBot.add(btnMod); barraBot.add(btnElim);

        add(barraTop, BorderLayout.NORTH);
        add(scroll,   BorderLayout.CENTER);
        add(barraBot, BorderLayout.SOUTH);
    }

    private void cargarTabla() {
        modelo.setRowCount(0);
        try {
            for (Mascota m : MascotaDB.listar())
                modelo.addRow(new Object[]{
                    m.getId(),
                    m.getAnimal().getEspecie(),
                    m.getAnimal().getNombre(),
                    m.getAnimal().getRaza(),
                    m.getAnimal().getEdad(),
                    m.getAnimal().getSexo(),
                    m.getAnimal().getPeso(),
                    m.getDueno().getNombre(),
                    m.getObservaciones()
                });
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar mascotas: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        modelo.setRowCount(0);
        try {
            for (Mascota m : MascotaDB.listar()) {
                if (m.getAnimal().getNombre().toLowerCase().contains(texto))
                    modelo.addRow(new Object[]{
                        m.getId(), m.getAnimal().getEspecie(),
                        m.getAnimal().getNombre(), m.getAnimal().getRaza(),
                        m.getAnimal().getEdad(), m.getAnimal().getSexo(),
                        m.getAnimal().getPeso(), m.getDueno().getNombre(), m.getObservaciones()
                    });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** RF-06: selecciona la mascota de la tabla y abre el formulario con sus datos. */
    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una mascota."); return; }
        try {
            Mascota m = MascotaDB.buscarPorId((int) modelo.getValueAt(fila, 0));
            if (m != null) abrirFormulario(m);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormulario(Mascota existente) {
        // Cargar dueños existentes para el selector
        List<vetcare.modelo.Dueno> duenos;
        try {
            duenos = DuenoDB.listar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar dueños: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (duenos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay dueños registrados. Registra un dueño primero.");
            return;
        }

        JComboBox<String> cbEspecie = new JComboBox<>(new String[]{"Perro", "Gato", "Ave"});
        JTextField fNombre  = new JTextField(20);
        JTextField fRaza    = new JTextField(20);
        JTextField fEdad    = new JTextField(5);
        JComboBox<String> cbSexo = new JComboBox<>(new String[]{"Macho", "Hembra"});
        JTextField fPeso    = new JTextField(8);
        JComboBox<String> cbDueno = new JComboBox<>();
        cbDueno.addItem("-- Seleccionar dueño --");
        duenos.forEach(d -> cbDueno.addItem(d.getDni() + " — " + d.getNombre()));
        JTextField fObs     = new JTextField(20);
        JLabel     lblExtra = new JLabel("Tamaño (Pequeño/Mediano/Grande):");
        JTextField fExtra   = new JTextField(20);

        cbEspecie.addActionListener(e -> {
            String esp = (String) cbEspecie.getSelectedItem();
            if ("Perro".equals(esp))     lblExtra.setText("Tamaño (Pequeño/Mediano/Grande):");
            else if ("Gato".equals(esp)) lblExtra.setText("Castrado? (true/false):");
            else                          lblExtra.setText("Tipo de pico (Curvo/Recto/Ganchudo):");
        });

        if (existente != null) {
            vetcare.modelo.Animal a = existente.getAnimal();
            cbEspecie.setSelectedItem(a.getEspecie());
            fNombre.setText(a.getNombre());
            fRaza.setText(a.getRaza());
            fEdad.setText(String.valueOf(a.getEdad()));
            cbSexo.setSelectedItem(a.getSexo());
            fPeso.setText(String.valueOf(a.getPeso()));
            for (int i = 0; i < duenos.size(); i++)
                if (duenos.get(i).getDni().equals(existente.getDueno().getDni())) cbDueno.setSelectedIndex(i + 1);
            fObs.setText(existente.getObservaciones() == null ? "" : existente.getObservaciones());
            if (a instanceof vetcare.modelo.Perro)     fExtra.setText(((vetcare.modelo.Perro) a).getTamano());
            else if (a instanceof vetcare.modelo.Gato) fExtra.setText(String.valueOf(((vetcare.modelo.Gato) a).isEsCastrado()));
            else if (a instanceof vetcare.modelo.Ave)  fExtra.setText(((vetcare.modelo.Ave) a).getTipoPico());
        }

        Object[] campos = {
            "Especie:", cbEspecie,
            "Nombre:", fNombre, "Raza:", fRaza,
            "Edad (años):", fEdad, "Sexo:", cbSexo,
            "Peso (kg):", fPeso,
            "Dueño (DNI — Nombre):", cbDueno,
            "Observaciones:", fObs,
            lblExtra, fExtra
        };
        int r = JOptionPane.showConfirmDialog(this, campos, existente == null ? "Registrar Mascota" : "Modificar Mascota",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String especie = (String) cbEspecie.getSelectedItem();
        String nombre  = fNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            return;
        }
        if (cbDueno.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Selecciona el dueño de la mascota.");
            return;
        }
        String dniDueno = duenos.get(cbDueno.getSelectedIndex() - 1).getDni();
        try {
            int    edad  = Integer.parseInt(fEdad.getText().trim().isEmpty() ? "0" : fEdad.getText().trim());
            double peso  = Double.parseDouble(fPeso.getText().trim().isEmpty() ? "0" : fPeso.getText().trim());
            String sexo  = (String) cbSexo.getSelectedItem();
            if (existente == null)
                MascotaDB.insertar(especie, nombre, fRaza.getText().trim(), edad, sexo, peso,
                    dniDueno, fObs.getText().trim(), fExtra.getText().trim());
            else
                MascotaDB.actualizar(existente.getId(), especie, nombre, fRaza.getText().trim(), edad, sexo, peso,
                    dniDueno, fObs.getText().trim(), fExtra.getText().trim());
            cargarTabla();
            JOptionPane.showMessageDialog(this, existente == null ? "Mascota registrada correctamente." : "Mascota modificada correctamente.");
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Edad y peso deben ser números válidos.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this, "Selecciona una mascota."); return; }
        int id  = (int) modelo.getValueAt(fila, 0);
        String nom = (String) modelo.getValueAt(fila, 2);
        int conf = JOptionPane.showConfirmDialog(this,
            "Eliminar a " + nom + " y todas sus citas/consultas?",
            "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            MascotaDB.eliminar(id);
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
