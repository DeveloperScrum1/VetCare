package vetcare.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {

    private static final String URL     =
        "jdbc:mysql://localhost:3306/vetcare_db" +
        "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String CLAVE   = "vetcare2026"; // contrasena del contenedor Docker

    private static Connection conexion;

    public static Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed())
            conexion = DriverManager.getConnection(URL, USUARIO, CLAVE);
        return conexion;
    }

    public static void cerrar() {
        try {
            if (conexion != null && !conexion.isClosed())
                conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
