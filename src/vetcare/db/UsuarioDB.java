package vetcare.db;

import vetcare.util.Seguridad;

import java.sql.*;

/** Usuarios del sistema (RF-01, RF-03, RF-04). Las contraseñas se guardan cifradas. */
public class UsuarioDB {

    public static final String ROL_ADMIN = "admin";
    public static final String ROL_RECEPCIONISTA = "recepcionista";

    /** Crea la tabla si no existe y, si está vacía, crea el usuario inicial admin. */
    public static void asegurarTabla() throws SQLException {
        try (Statement st = ConexionDB.getConexion().createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "usuario VARCHAR(50) NOT NULL UNIQUE, " +
                "clave VARCHAR(200) NOT NULL, " +
                "rol VARCHAR(20) NOT NULL)");
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM usuarios")) {
                if (rs.next() && rs.getInt(1) == 0) registrar("admin", "vetcare2026", ROL_ADMIN);
            }
        }
    }

    /** Devuelve el rol si las credenciales son correctas; null en caso contrario. */
    public static String autenticar(String usuario, String clave) throws SQLException {
        String sql = "SELECT clave, rol FROM usuarios WHERE usuario = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && Seguridad.verificar(clave, rs.getString("clave"))) return rs.getString("rol");
            }
        }
        return null;
    }

    public static boolean existe(String usuario) throws SQLException {
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement("SELECT COUNT(*) FROM usuarios WHERE usuario = ?")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }

    public static void registrar(String usuario, String clave, String rol) throws SQLException {
        if (usuario == null || usuario.trim().isEmpty()) throw new IllegalArgumentException("El usuario no puede estar vacío.");
        if (clave == null || clave.length() < 6) throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        if (!ROL_ADMIN.equals(rol) && !ROL_RECEPCIONISTA.equals(rol)) throw new IllegalArgumentException("Rol no válido.");
        if (existe(usuario.trim())) throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement("INSERT INTO usuarios (usuario, clave, rol) VALUES (?,?,?)")) {
            ps.setString(1, usuario.trim());
            ps.setString(2, Seguridad.cifrar(clave));
            ps.setString(3, rol);
            ps.executeUpdate();
        }
    }

    /** Cambia la contraseña si la actual es correcta. Devuelve false si la actual no coincide. */
    public static boolean cambiarClave(String usuario, String actual, String nueva) throws SQLException {
        if (nueva == null || nueva.length() < 6) throw new IllegalArgumentException("La contraseña nueva debe tener al menos 6 caracteres.");
        if (autenticar(usuario, actual) == null) return false;
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement("UPDATE usuarios SET clave = ? WHERE usuario = ?")) {
            ps.setString(1, Seguridad.cifrar(nueva));
            ps.setString(2, usuario);
            ps.executeUpdate();
        }
        return true;
    }

    public static void eliminar(String usuario) throws SQLException {
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement("DELETE FROM usuarios WHERE usuario = ?")) {
            ps.setString(1, usuario);
            ps.executeUpdate();
        }
    }
}
