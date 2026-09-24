package vetcare.modelo;

public class Veterinario extends Persona {
    private int     id;
    private String  especialidad;
    private String  colegiatura;
    private boolean activo;

    public Veterinario(int id, String nombre, String dni, String telefono,
                       String correo, String especialidad, String colegiatura) {
        super(nombre, dni, telefono, correo);
        this.id           = id;
        this.especialidad = especialidad;
        this.colegiatura  = colegiatura;
        this.activo       = true;
    }

    public int     getId()           { return id; }
    public String  getEspecialidad() { return especialidad; }
    public String  getColegiatura()  { return colegiatura; }
    public boolean isActivo()        { return activo; }

    public void desactivar() { this.activo = false; }
    public void activar()    { this.activo = true; }

    public String toLinea() {
        return id + "|" + getNombre() + "|" + getDni() + "|" + getTelefono()
                + "|" + getCorreo() + "|" + especialidad + "|" + colegiatura + "|" + activo;
    }

    public static Veterinario fromLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        Veterinario v = new Veterinario(
                Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(),
                p[3].trim(), p[4].trim(), p[5].trim(), p[6].trim());
        if (!Boolean.parseBoolean(p[7].trim())) v.desactivar();
        return v;
    }

    @Override
    public String toString() {
        return "[" + id + "] Dr/a. " + getNombre() + " — " + especialidad
                + " | Colegiatura: " + colegiatura + (activo ? "" : " [INACTIVO]");
    }
}
