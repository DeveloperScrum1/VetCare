package vetcare.db;

import vetcare.modelo.Dueno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuenoDB {

    public static List<Dueno> listar() throws SQLException {
        List<Dueno> lista = new ArrayList<>();
        String sql = "SELECT * FROM duenos ORDER BY id";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(mapear(rs));
        }
        return lista;
    }

    public static void insertar(Dueno d) throws SQLException {
        String sql = "INSERT INTO duenos (nombre, dni, telefono, correo, direccion) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getDni());
            ps.setString(3, d.getTelefono());
            ps.setString(4, d.getCorreo());
            ps.setString(5, d.getDireccion());
            ps.executeUpdate();
        }
    }

    public static void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM duenos WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static boolean existeDni(String dni) throws SQLException {
        String sql = "SELECT COUNT(*) FROM duenos WHERE dni = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static Dueno buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT * FROM duenos WHERE dni = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private static Dueno mapear(ResultSet rs) throws SQLException {
        return new Dueno(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("dni"),
            rs.getString("telefono"),
            rs.getString("correo"),
            rs.getString("direccion")
        );
    }

    /** RF-17: modifica los datos de contacto de un dueño (el DNI no se cambia porque lo usan las mascotas). */
    public static void actualizar(int id, String nombre, String telefono, String correo, String direccion) throws SQLException {
        String sql = "UPDATE duenos SET nombre=?, telefono=?, correo=?, direccion=? WHERE id=?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, telefono);
            ps.setString(3, correo);
            ps.setString(4, direccion);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }
}
