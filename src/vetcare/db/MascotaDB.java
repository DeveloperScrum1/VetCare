package vetcare.db;

import vetcare.modelo.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaDB {

    public static List<Mascota> listar() throws SQLException {
        List<Mascota> lista = new ArrayList<>();
        String sql = "SELECT m.*, d.nombre AS nom_dueno, d.telefono AS tel_dueno, " +
                     "d.correo AS cor_dueno, d.direccion AS dir_dueno " +
                     "FROM mascotas m JOIN duenos d ON m.dni_dueno = d.dni ORDER BY m.id";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next())
                lista.add(mapear(rs));
        }
        return lista;
    }

    public static void insertar(String especie, String nombre, String raza, int edad,
                                String sexo, double peso, String dniDueno,
                                String observaciones, String atributoExtra) throws SQLException {
        String sql = "INSERT INTO mascotas (especie, nombre, raza, edad, sexo, peso, " +
                     "dni_dueno, observaciones, atributo_extra) VALUES (?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, especie);
            ps.setString(2, nombre);
            ps.setString(3, raza);
            ps.setInt(4, edad);
            ps.setString(5, sexo);
            ps.setDouble(6, peso);
            ps.setString(7, dniDueno);
            ps.setString(8, observaciones);
            ps.setString(9, atributoExtra);
            ps.executeUpdate();
        }
    }

    public static void eliminar(int id) throws SQLException {
        // Eliminar consultas primero (FK)
        try (PreparedStatement ps = ConexionDB.getConexion()
                .prepareStatement("DELETE FROM consultas WHERE id_mascota = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
        // Eliminar citas (FK)
        try (PreparedStatement ps = ConexionDB.getConexion()
                .prepareStatement("DELETE FROM citas WHERE id_mascota = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
        try (PreparedStatement ps = ConexionDB.getConexion()
                .prepareStatement("DELETE FROM mascotas WHERE id = ?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    public static Mascota buscarPorId(int id) throws SQLException {
        String sql = "SELECT m.*, d.nombre AS nom_dueno, d.telefono AS tel_dueno, " +
                     "d.correo AS cor_dueno, d.direccion AS dir_dueno " +
                     "FROM mascotas m JOIN duenos d ON m.dni_dueno = d.dni WHERE m.id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    private static Mascota mapear(ResultSet rs) throws SQLException {
        String especie      = rs.getString("especie");
        String nombre       = rs.getString("nombre");
        String raza         = rs.getString("raza");
        int    edad         = rs.getInt("edad");
        String sexo         = rs.getString("sexo");
        double peso         = rs.getDouble("peso");
        String extra        = rs.getString("atributo_extra");

        Animal animal;
        switch (especie) {
            case "Perro" -> animal = new Perro(nombre, raza, edad, sexo, peso, extra);
            case "Gato"  -> animal = new Gato(nombre, raza, edad, sexo, peso,
                                               Boolean.parseBoolean(extra));
            case "Ave"   -> animal = new Ave(nombre, raza, edad, sexo, peso, extra);
            default      -> throw new SQLException("Especie desconocida: " + especie);
        }

        Dueno dueno = new Dueno(
            0, rs.getString("nom_dueno"), rs.getString("dni_dueno"),
            rs.getString("tel_dueno"), rs.getString("cor_dueno"), rs.getString("dir_dueno")
        );
        return new Mascota(rs.getInt("id"), animal, dueno, rs.getString("observaciones"));
    }

    /** RF-06: modifica los datos de una mascota existente. */
    public static void actualizar(int id, String especie, String nombre, String raza, int edad,
                                  String sexo, double peso, String dniDueno,
                                  String observaciones, String atributoExtra) throws SQLException {
        String sql = "UPDATE mascotas SET especie=?, nombre=?, raza=?, edad=?, sexo=?, peso=?, " +
                     "dni_dueno=?, observaciones=?, atributo_extra=? WHERE id=?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, especie);
            ps.setString(2, nombre);
            ps.setString(3, raza);
            ps.setInt(4, edad);
            ps.setString(5, sexo);
            ps.setDouble(6, peso);
            ps.setString(7, dniDueno);
            ps.setString(8, observaciones);
            ps.setString(9, atributoExtra);
            ps.setInt(10, id);
            ps.executeUpdate();
        }
    }
}
