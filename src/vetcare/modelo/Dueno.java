package vetcare.modelo;

import java.util.ArrayList;
import java.util.List;

public class Dueno extends Persona {
    private int    id;
    private String direccion;
    private List<String> mascotas; // nombres de mascotas registradas

    public Dueno(int id, String nombre, String dni, String telefono, String correo, String direccion) {
        super(nombre, dni, telefono, correo);
        this.id        = id;
        this.direccion = direccion;
        this.mascotas  = new ArrayList<>();
    }

    public int    getId()        { return id; }
    public String getDireccion() { return direccion; }

    public void setDireccion(String direccion) { this.direccion = direccion; }

    public void agregarMascota(String nombreMascota) {
        mascotas.add(nombreMascota);
    }

    public List<String> getMascotas() { return mascotas; }

    public String toLinea() {
        return id + "|" + getNombre() + "|" + getDni() + "|" + getTelefono()
                + "|" + getCorreo() + "|" + direccion;
    }

    public static Dueno fromLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        return new Dueno(
                Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(),
                p[3].trim(), p[4].trim(), p[5].trim());
    }

    @Override
    public String toString() {
        return "[" + id + "] " + getNombre() + " — DNI: " + getDni()
                + " | Tel: " + getTelefono() + " | Dir: " + direccion;
    }
}
