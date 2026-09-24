package vetcare.modelo;

import java.util.ArrayList;
import java.util.List;

public class Mascota {
    private int                     id;
    private Animal                  animal;
    private Dueno                   dueno;
    private String                  observaciones;
    private List<ConsultaMedica>    historial;

    public Mascota(int id, Animal animal, Dueno dueno, String observaciones) {
        if (animal == null) throw new IllegalArgumentException("El animal no puede ser nulo.");
        if (dueno  == null) throw new IllegalArgumentException("El dueño no puede ser nulo.");
        this.id            = id;
        this.animal        = animal;
        this.dueno         = dueno;
        this.observaciones = observaciones;
        this.historial     = new ArrayList<>();
    }

    public int               getId()            { return id; }
    public Animal            getAnimal()        { return animal; }
    public Dueno             getDueno()         { return dueno; }
    public String            getObservaciones() { return observaciones; }
    public List<ConsultaMedica> getHistorial()  { return historial; }

    public void setObservaciones(String obs) { this.observaciones = obs; }

    public void agregarConsulta(ConsultaMedica consulta) {
        historial.add(consulta);
    }

    public void mostrarHistorial() {
        if (historial.isEmpty()) {
            System.out.println("    Sin consultas médicas registradas.");
            return;
        }
        for (int i = historial.size() - 1; i >= 0; i--)
            System.out.println(historial.get(i));
    }

    @Override
    public String toString() {
        return "[" + id + "] " + animal + " | Dueño: " + dueno.getNombre()
                + (observaciones != null && !observaciones.isEmpty()
                   ? " | Obs: " + observaciones : "");
    }
}
