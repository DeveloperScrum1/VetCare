package vetcare.gui;

import vetcare.db.ConexionDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    static final Color VERDE      = new Color(0x14, 0x5A, 0x32);
    static final Color VERDE_MED  = new Color(0x1E, 0x8B, 0x4F);
    static final Color AMBAR      = new Color(0xB7, 0x7A, 0x0C);
    static final Color FONDO      = new Color(0xF4, 0xF6, 0xF7);
    static final Color VERDE_CLAR = new Color(0xEA, 0xFA, 0xF1);

    public MainFrame() {
        setTitle("VetCare — Sistema de Gestión de Clínica Veterinaria");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1050, 680);
        setLocationRelativeTo(null);
        construirUI();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int r = JOptionPane.showConfirmDialog(MainFrame.this,
                    "Desea cerrar VetCare?", "Confirmar salida",
                    JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    ConexionDB.cerrar();
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private void construirUI() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(VERDE);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel lblTitulo = new JLabel("  VETCARE", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Calibri", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        JLabel lblSub = new JLabel("UPN 2026", SwingConstants.RIGHT);
        lblSub.setFont(new Font("Calibri", Font.PLAIN, 12));
        lblSub.setForeground(new Color(0xD5, 0xF5, 0xE3));
        header.add(lblTitulo, BorderLayout.WEST);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);
        JLabel lblUsuario = new JLabel("Usuario: " + Sesion.getUsuario() + " (" + Sesion.getRol() + ")");
        lblUsuario.setFont(new Font("Calibri", Font.BOLD, 12));
        lblUsuario.setForeground(Color.WHITE);
        derecha.add(lblUsuario);
        derecha.add(botonCabecera("Cambiar contraseña", e -> cambiarClave()));
        if (Sesion.esAdmin()) derecha.add(botonCabecera("Nuevo usuario", e -> nuevoUsuario()));
        derecha.add(botonCabecera("Cerrar sesión", e -> cerrarSesion()));
        derecha.add(lblSub);
        header.add(derecha, BorderLayout.EAST);

        // Pestanas
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Calibri", Font.BOLD, 13));
        tabs.setBackground(FONDO);
        tabs.addTab("Mascotas",      new PanelMascotas());
        tabs.addTab("Dueños",        new PanelDuenos());
        tabs.addTab("Veterinarios",  new PanelVeterinarios());
        tabs.addTab("Citas",         new PanelCitas());
        tabs.addTab("Historial",     new PanelHistorial());

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(AMBAR);
        JLabel pie = new JLabel("Conectado a vetcare_db  |  Grupo 2 — Proyecto Final  |  TPOO UPN 2026");
        pie.setForeground(Color.WHITE);
        pie.setFont(new Font("Calibri", Font.PLAIN, 11));
        footer.add(pie);

        contenedor.add(header, BorderLayout.NORTH);
        contenedor.add(tabs, BorderLayout.CENTER);
        contenedor.add(footer, BorderLayout.SOUTH);
        add(contenedor);
    }

    private JButton botonCabecera(String texto, java.awt.event.ActionListener accion) {
        JButton b = new JButton(texto);
        b.setBackground(new Color(0x1E, 0x7B, 0x4A)); b.setForeground(Color.WHITE);
        b.setFont(new Font("Calibri", Font.BOLD, 11));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(accion);
        return b;
    }

    /** RF-02: cierra la sesión y vuelve a la ventana de inicio de sesión. */
    private void cerrarSesion() {
        int r = JOptionPane.showConfirmDialog(this, "¿Cerrar la sesión de " + Sesion.getUsuario() + "?",
            "Cerrar sesión", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        Sesion.cerrar();
        dispose();
        new LoginFrame().setVisible(true);
    }

    /** RF-03: cambia la contraseña del usuario actual pidiendo la clave antigua. */
    private void cambiarClave() {
        JPasswordField actual = new JPasswordField(18), nueva = new JPasswordField(18), confirmar = new JPasswordField(18);
        Object[] campos = { "Contraseña actual:", actual, "Contraseña nueva (mínimo 6 caracteres):", nueva, "Repetir contraseña nueva:", confirmar };
        int r = JOptionPane.showConfirmDialog(this, campos, "Cambiar contraseña", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        String n = new String(nueva.getPassword());
        if (!n.equals(new String(confirmar.getPassword()))) {
            JOptionPane.showMessageDialog(this, "La contraseña nueva y su repetición no coinciden.");
            return;
        }
        try {
            if (vetcare.db.UsuarioDB.cambiarClave(Sesion.getUsuario(), new String(actual.getPassword()), n))
                JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente.");
            else
                JOptionPane.showMessageDialog(this, "La contraseña actual es incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** RF-04: el administrador crea usuarios con rol admin o recepcionista. */
    private void nuevoUsuario() {
        if (!Sesion.esAdmin()) { JOptionPane.showMessageDialog(this, "Solo el administrador puede registrar usuarios."); return; }
        JTextField usuario = new JTextField(18);
        JPasswordField clave = new JPasswordField(18), confirmar = new JPasswordField(18);
        JComboBox<String> rol = new JComboBox<>(new String[]{ "recepcionista", "admin" });
        Object[] campos = { "Usuario:", usuario, "Contraseña (mínimo 6 caracteres):", clave, "Repetir contraseña:", confirmar, "Rol:", rol };
        int r = JOptionPane.showConfirmDialog(this, campos, "Registrar usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;
        if (!new String(clave.getPassword()).equals(new String(confirmar.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden.");
            return;
        }
        try {
            vetcare.db.UsuarioDB.registrar(usuario.getText(), new String(clave.getPassword()), (String) rol.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.");
        } catch (IllegalArgumentException | java.sql.SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
