package vetcare.gestion;

import vetcare.modelo.*;
import java.util.ArrayList;
import java.util.List;

public class GestorMascotas implements IGestionable<Mascota> {
    private ArrayList<Mascota> mascotas;
    private int                siguienteId;

    public GestorMascotas() {
        mascotas    = new ArrayList<>();
        siguienteId = 1;
    }

    public void agregar(Mascota m) {
        mascotas.add(m);
        if (m.getId() >= siguienteId) siguienteId = m.getId() + 1;
    }

    @Override
    public void registrar(Mascota m) { agregar(m); }

    public int getSiguienteId() { return siguienteId++; }

    public List<Mascota> getTodas() { return mascotas; }

    public Mascota buscarPorId(int id) {
        for (Mascota m : mascotas)
            if (m.getId() == id) return m;
        return null;
    }

    @Override
    public Mascota buscar(int id) { return buscarPorId(id); }

    public List<Mascota> buscarPorNombre(String nombre) {
        List<Mascota> resultado = new ArrayList<>();
        for (Mascota m : mascotas)
            if (m.getAnimal().getNombre().toLowerCase().contains(nombre.toLowerCase()))
                resultado.add(m);
        return resultado;
    }

    public List<Mascota> buscarPorDuenoDni(String dni) {
        List<Mascota> resultado = new ArrayList<>();
        for (Mascota m : mascotas)
            if (m.getDueno().getDni().equals(dni))
                resultado.add(m);
        return resultado;
    }

    public List<Mascota> filtrarPorEspecie(String especie) {
        List<Mascota> resultado = new ArrayList<>();
        for (Mascota m : mascotas)
            if (m.getAnimal().getEspecie().equalsIgnoreCase(especie))
                resultado.add(m);
        return resultado;
    }

    public boolean eliminar(int id) {
        return mascotas.removeIf(m -> m.getId() == id);
    }

    public void listar() {
        if (mascotas.isEmpty()) {
            System.out.println("  No hay mascotas registradas.");
            return;
        }
        for (Mascota m : mascotas)
            System.out.println("  " + m);
    }
}
