package vetcare.db;

import vetcare.modelo.Veterinario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDB {

    public static List<Veterinario> listar() throws SQLException {
        List<Veterinario> lista = new ArrayList<>();
        String sql = "SELECT * FROM veterinarios ORDER BY id";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(mapear(rs));
        }
        return lista;
    }

    public static void insertar(Veterinario v) throws SQLException {
        String sql = "INSERT INTO veterinarios (nombre, dni, telefono, correo, especialidad, colegiatura, activo) " +
                     "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, v.getNombre());
            ps.setString(2, v.getDni());
            ps.setString(3, v.getTelefono());
            ps.setString(4, v.getCorreo());
            ps.setString(5, v.getEspecialidad());
            ps.setString(6, v.getColegiatura());
            ps.setBoolean(7, v.isActivo());
            ps.executeUpdate();
        }
    }

    public static void actualizarActivo(int id, boolean activo) throws SQLException {
        String sql = "UPDATE veterinarios SET activo = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM veterinarios WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static Veterinario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM veterinarios WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private static Veterinario mapear(ResultSet rs) throws SQLException {
        Veterinario v = new Veterinario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("dni"),
            rs.getString("telefono"),
            rs.getString("correo"),
            rs.getString("especialidad"),
            rs.getString("colegiatura")
        );
        if (!rs.getBoolean("activo")) v.desactivar();
        return v;
    }

    /** RF-33: modifica los datos de un veterinario existente. */
    public static void actualizar(int id, String nombre, String dni, String telefono, String correo,
                                  String especialidad, String colegiatura) throws SQLException {
        String sql = "UPDATE veterinarios SET nombre=?, dni=?, telefono=?, correo=?, especialidad=?, colegiatura=? WHERE id=?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, dni);
            ps.setString(3, telefono);
            ps.setString(4, correo);
            ps.setString(5, especialidad);
            ps.setString(6, colegiatura);
            ps.setInt(7, id);
            ps.executeUpdate();
        }
    }
}
