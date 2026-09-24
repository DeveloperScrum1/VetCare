package vetcare.gestion;

import vetcare.modelo.Cita;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorCitas implements IGestionable<Cita> {
    private ArrayList<Cita> citas;
    private int             siguienteId;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public GestorCitas() {
        citas       = new ArrayList<>();
        siguienteId = 1;
    }

    public void agregar(Cita c) {
        citas.add(c);
        if (c.getId() >= siguienteId) siguienteId = c.getId() + 1;
    }

    @Override
    public void registrar(Cita c) { agregar(c); }

    public int getSiguienteId() { return siguienteId++; }

    public List<Cita> getTodas() { return citas; }

    public Cita buscarPorId(int id) {
        for (Cita c : citas)
            if (c.getId() == id) return c;
        return null;
    }

    @Override
    public Cita buscar(int id) { return buscarPorId(id); }

    public boolean hayConflicto(String fecha, String hora, int idVeterinario) {
        for (Cita c : citas)
            if (c.tieneConflicto(fecha, hora, idVeterinario)) return true;
        return false;
    }

    public List<Cita> citasDelDia() {
        String hoy = LocalDate.now().format(FMT);
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas)
            if (c.getFecha().equals(hoy) && c.getEstado() == Cita.Estado.PENDIENTE)
                resultado.add(c);
        return resultado;
    }

    public List<Cita> pendientes() {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas)
            if (c.getEstado() == Cita.Estado.PENDIENTE)
                resultado.add(c);
        return resultado;
    }

    public List<Cita> porMascota(int idMascota) {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas)
            if (c.getMascota().getId() == idMascota)
                resultado.add(c);
        return resultado;
    }

    public List<Cita> porFecha(String fecha) {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas)
            if (c.getFecha().equals(fecha))
                resultado.add(c);
        return resultado;
    }

    public List<Cita> agendaVeterinario(int idVet) {
        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas)
            if (c.getVeterinario().getId() == idVet && c.getEstado() == Cita.Estado.PENDIENTE)
                resultado.add(c);
        return resultado;
    }

    public void listar() {
        if (citas.isEmpty()) {
            System.out.println("  No hay citas registradas.");
            return;
        }
        for (Cita c : citas)
            System.out.println("  " + c);
    }
}
