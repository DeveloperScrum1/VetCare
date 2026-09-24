package vetcare.gui;

import vetcare.db.ConexionDB;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {

    private static final Color VERDE  = new Color(0x14, 0x5A, 0x32);
    private static final Color AMBAR  = new Color(0xB7, 0x7A, 0x0C);
    private static final Color FONDO  = new Color(0xF4, 0xF6, 0xF7);

    private JTextField     txtUsuario;
    private JPasswordField txtClave;
    private JLabel         lblMensaje;

    public LoginFrame() {
        setTitle("VetCare — Inicio de sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(420, 380);
        setLocationRelativeTo(null);
        construirUI();
    }

    private void construirUI() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(VERDE);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel titulo = new JLabel("VETCARE", SwingConstants.CENTER);
        titulo.setFont(new Font("Calibri", Font.BOLD, 30));
        titulo.setForeground(Color.WHITE);
        JLabel subtitulo = new JLabel("Sistema de Gestión de Clínica Veterinaria", SwingConstants.CENTER);
        subtitulo.setFont(new Font("Calibri", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(0xD5, 0xF5, 0xE3));
        JPanel headerTextos = new JPanel(new GridLayout(2, 1, 0, 4));
        headerTextos.setBackground(VERDE);
        headerTextos.add(titulo);
        headerTextos.add(subtitulo);
        header.add(headerTextos);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(FONDO);
        form.setBorder(new EmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);

        JLabel lblU = new JLabel("Usuario:");
        lblU.setFont(new Font("Calibri", Font.BOLD, 13));
        lblU.setForeground(VERDE);
        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("Calibri", Font.PLAIN, 13));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VERDE, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        JLabel lblC = new JLabel("Contraseña:");
        lblC.setFont(new Font("Calibri", Font.BOLD, 13));
        lblC.setForeground(VERDE);
        txtClave = new JPasswordField();
        txtClave.setFont(new Font("Calibri", Font.PLAIN, 13));
        txtClave.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(VERDE, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));

        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setForeground(new Color(0xC0, 0x39, 0x2B));
        lblMensaje.setFont(new Font("Calibri", Font.BOLD, 12));

        JButton btnLogin = new JButton("Ingresar");
        btnLogin.setBackground(VERDE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Calibri", Font.BOLD, 14));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.setPreferredSize(new Dimension(0, 38));

        gbc.gridx = 0; gbc.gridy = 0; form.add(lblU, gbc);
        gbc.gridy = 1; form.add(txtUsuario, gbc);
        gbc.gridy = 2; form.add(lblC, gbc);
        gbc.gridy = 3; form.add(txtClave, gbc);
        gbc.gridy = 4; form.add(lblMensaje, gbc);
        gbc.gridy = 5; form.add(btnLogin, gbc);

        contenedor.add(header, BorderLayout.NORTH);
        contenedor.add(form, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel();
        footer.setBackground(AMBAR);
        JLabel pie = new JLabel("UPN 2026 — Técnicas de Programación OO");
        pie.setForeground(Color.WHITE);
        pie.setFont(new Font("Calibri", Font.PLAIN, 11));
        footer.add(pie);
        contenedor.add(footer, BorderLayout.SOUTH);

        add(contenedor);

        // Evento login
        ActionListener accionLogin = e -> intentarLogin();
        btnLogin.addActionListener(accionLogin);
        txtClave.addActionListener(accionLogin);
        txtUsuario.addActionListener(e -> txtClave.requestFocus());
    }

    private int intentos = 3;

    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String clave   = new String(txtClave.getPassword()).trim();

        String rol = null;
        boolean conexionOk = true;
        {
            try {
                ConexionDB.getConexion(); // verifica conexion a DB
                vetcare.db.UsuarioDB.asegurarTabla();
                rol = vetcare.db.UsuarioDB.autenticar(usuario, clave);
            } catch (Exception ex) {
                conexionOk = false;
                JOptionPane.showMessageDialog(this,
                    "No se pudo conectar a MySQL.\n" +
                    "Verifica que MySQL esté corriendo y que la base de datos\n" +
                    "'vetcare_db' exista (ejecuta vetcare_db.sql).\n\n" +
                    "Error: " + ex.getMessage(),
                    "Error de conexión", JOptionPane.ERROR_MESSAGE);
            }
        }
        if (!conexionOk) return;
        if (rol != null) {
            Sesion.iniciar(usuario, rol);
            dispose();
            new MainFrame().setVisible(true);
        } else {
            intentos--;
            if (intentos > 0) {
                lblMensaje.setText("Credenciales incorrectas. Intentos restantes: " + intentos);
            } else {
                lblMensaje.setText("Acceso bloqueado. Reinicia la aplicación.");
                txtUsuario.setEnabled(false);
                txtClave.setEnabled(false);
            }
        }
    }
}
