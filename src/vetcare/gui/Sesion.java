package vetcare.gui;

/** Usuario que inició sesión en la aplicación gráfica. */
public final class Sesion {
    private static String usuario;
    private static String rol;

    private Sesion() { }

    public static void iniciar(String u, String r) { usuario = u; rol = r; }
    public static void cerrar() { usuario = null; rol = null; }
    public static String getUsuario() { return usuario; }
    public static String getRol() { return rol; }
    public static boolean esAdmin() { return "admin".equals(rol); }
}
