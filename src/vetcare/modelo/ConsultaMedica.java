package vetcare.modelo;

public class ConsultaMedica {
    private int    id;
    private String fecha;
    private String diagnostico;
    private String tratamiento;
    private String observaciones;

    // Sobrecarga 1: solo diagnóstico
    public ConsultaMedica(int id, String fecha, String diagnostico) {
        this(id, fecha, diagnostico, "Sin tratamiento indicado", "");
    }

    // Sobrecarga 2: diagnóstico + tratamiento
    public ConsultaMedica(int id, String fecha, String diagnostico, String tratamiento) {
        this(id, fecha, diagnostico, tratamiento, "");
    }

    // Sobrecarga 3: diagnóstico + tratamiento + observaciones (constructor base)
    public ConsultaMedica(int id, String fecha, String diagnostico,
                          String tratamiento, String observaciones) {
        this.id            = id;
        this.fecha         = fecha;
        this.diagnostico   = diagnostico;
        this.tratamiento   = tratamiento;
        this.observaciones = observaciones;
    }

    public int    getId()            { return id; }
    public String getFecha()         { return fecha; }
    public String getDiagnostico()   { return diagnostico; }
    public String getTratamiento()   { return tratamiento; }
    public String getObservaciones() { return observaciones; }

    public String toLinea() {
        return id + "|" + fecha + "|" + diagnostico + "|" + tratamiento + "|" + observaciones;
    }

    public static ConsultaMedica fromLinea(String linea) {
        String[] p = linea.split("\\|", -1);
        return new ConsultaMedica(
                Integer.parseInt(p[0].trim()), p[1].trim(),
                p[2].trim(), p[3].trim(), p[4].trim());
    }

    @Override
    public String toString() {
        String out = "  Consulta #" + id + " [" + fecha + "]\n"
                   + "    Diagnóstico : " + diagnostico + "\n"
                   + "    Tratamiento : " + tratamiento;
        if (observaciones != null && !observaciones.isEmpty())
            out += "\n    Observaciones: " + observaciones;
        return out;
    }
}
