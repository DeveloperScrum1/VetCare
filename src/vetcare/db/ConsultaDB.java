package vetcare.db;

import vetcare.modelo.ConsultaMedica;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDB {

    public static List<ConsultaMedica> listarPorMascota(int idMascota) throws SQLException {
        List<ConsultaMedica> lista = new ArrayList<>();
        String sql = "SELECT * FROM consultas WHERE id_mascota = ? ORDER BY id DESC";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new ConsultaMedica(
                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("diagnostico"),
                        rs.getString("tratamiento"),
                        rs.getString("observaciones")
                    ));
                }
            }
        }
        return lista;
    }

    public static void insertar(int idMascota, String fecha,
                                String diagnostico, String tratamiento,
                                String observaciones) throws SQLException {
        String sql = "INSERT INTO consultas (id_mascota, fecha, diagnostico, tratamiento, observaciones) " +
                     "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ps.setString(2, fecha);
            ps.setString(3, diagnostico);
            ps.setString(4, tratamiento);
            ps.setString(5, observaciones);
            ps.executeUpdate();
        }
    }

    public static void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM consultas WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * RF-40: consultas realizadas entre dos fechas (inclusive). Las fechas se guardan como texto dd/MM/yyyy,
     * por eso el filtro se hace en Java y no con una comparación de texto en SQL.
     * Cada fila: fecha, mascota, diagnóstico, tratamiento, observaciones.
     */
    public static List<Object[]> listarPorPeriodo(java.time.LocalDate desde, java.time.LocalDate hasta) throws SQLException {
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT c.fecha, m.nombre AS mascota, c.diagnostico, c.tratamiento, c.observaciones " +
                     "FROM consultas c JOIN mascotas m ON c.id_mascota = m.id";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                java.time.LocalDate f;
                try { f = java.time.LocalDate.parse(rs.getString("fecha"), fmt); }
                catch (java.time.format.DateTimeParseException e) { continue; }
                if (!f.isBefore(desde) && !f.isAfter(hasta))
                    filas.add(new Object[]{ f, rs.getString("mascota"), rs.getString("diagnostico"),
                                            rs.getString("tratamiento"), rs.getString("observaciones") });
            }
        }
        filas.sort((a, b) -> ((java.time.LocalDate) a[0]).compareTo((java.time.LocalDate) b[0]));
        for (Object[] fila : filas) fila[0] = ((java.time.LocalDate) fila[0]).format(fmt);
        return filas;
    }
}
