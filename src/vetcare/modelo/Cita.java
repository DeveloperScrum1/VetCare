package vetcare.modelo;

public class Cita {
    public enum Estado { PENDIENTE, ATENDIDA, CANCELADA, NO_ASISTIO }

    private int         id;
    private Mascota     mascota;
    private Veterinario veterinario;
    private String      fecha;   // formato dd/MM/yyyy
    private String      hora;    // formato HH:mm
    private String      motivo;
    private Estado      estado;
    private String      motivoCancelacion;

    public Cita(int id, Mascota mascota, Veterinario veterinario,
                String fecha, String hora, String motivo) {
        this.id           = id;
        this.mascota      = mascota;
        this.veterinario  = veterinario;
        this.fecha        = fecha;
        this.hora         = hora;
        this.motivo       = motivo;
        this.estado       = Estado.PENDIENTE;
        this.motivoCancelacion = "";
    }

    public int         getId()          { return id; }
    public Mascota     getMascota()     { return mascota; }
    public Veterinario getVeterinario() { return veterinario; }
    public String      getFecha()       { return fecha; }
    public String      getHora()        { return hora; }
    public String      getMotivo()      { return motivo; }
    public Estado      getEstado()      { return estado; }
    public String      getMotivoCancelacion() { return motivoCancelacion; }

    public void setFecha(String fecha)   { this.fecha = fecha; }
    public void setHora(String hora)     { this.hora  = hora; }

    public void confirmarAsistencia() {
        if (estado != Estado.PENDIENTE)
            throw new IllegalStateException("Solo se puede confirmar una cita pendiente.");
        this.estado = Estado.ATENDIDA;
    }

    public void marcarNoAsistio() {
        this.estado = Estado.NO_ASISTIO;
    }

    public void cancelar(String motivo) {
        if (estado == Estado.ATENDIDA)
            throw new IllegalStateException("No se puede cancelar una cita ya atendida.");
        this.estado = Estado.CANCELADA;
        this.motivoCancelacion = motivo;
    }

    public boolean tieneConflicto(String fecha, String hora, int idVeterinario) {
        return this.estado == Estado.PENDIENTE
                && this.fecha.equals(fecha)
                && this.hora.equals(hora)
                && this.veterinario.getId() == idVeterinario;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + fecha + " " + hora
                + " | " + mascota.getAnimal().getNombre()
                + " → Dr/a. " + veterinario.getNombre()
                + " | Motivo: " + motivo
                + " | Estado: " + estado
                + (estado == Estado.CANCELADA ? " (" + motivoCancelacion + ")" : "");
    }
}
