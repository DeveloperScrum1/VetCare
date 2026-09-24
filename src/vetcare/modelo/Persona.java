package vetcare.modelo;

public abstract class Persona {
    private String nombre;
    private String dni;
    private String telefono;
    private String correo;

    public Persona(String nombre, String dni, String telefono, String correo) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (dni == null || dni.trim().isEmpty())
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        this.nombre   = nombre.trim();
        this.dni      = dni.trim();
        this.telefono = telefono;
        this.correo   = correo;
    }

    public String getNombre()   { return nombre; }
    public String getDni()      { return dni; }
    public String getTelefono() { return telefono; }
    public String getCorreo()   { return correo; }

    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setCorreo(String correo)     { this.correo   = correo; }

    @Override
    public String toString() {
        return nombre + " (DNI: " + dni + ")";
    }
}
