package vetcare.util;

import vetcare.gestion.GestorMascotas;
import vetcare.modelo.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de VetCare en archivos de texto UTF-8 (carpeta data/).
 * PC5: se agregan la recarga de mascotas y de citas, y el control de líneas inválidas.
 */
public class PersistenciaArchivos {

    private final String rutaDuenos;
    private final String rutaVeterinarios;
    private final String rutaMascotas;
    private final String rutaConsultas;
    private final String rutaCitas;
    private int lineasInvalidas;

    public PersistenciaArchivos(String carpetaData) {
        this.rutaDuenos       = carpetaData + File.separator + "duenos.txt";
        this.rutaVeterinarios = carpetaData + File.separator + "veterinarios.txt";
        this.rutaMascotas     = carpetaData + File.separator + "mascotas.txt";
        this.rutaConsultas    = carpetaData + File.separator + "consultas.txt";
        this.rutaCitas        = carpetaData + File.separator + "citas.txt";
        new File(carpetaData).mkdirs();
    }

    public int getLineasInvalidas() { return lineasInvalidas; }

    // ── Utilidades de lectura y escritura ─────────────────────────────────────
    private void escribir(String ruta, List<String> lineas, String etiqueta) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(ruta), StandardCharsets.UTF_8)) {
            for (String l : lineas) { bw.write(l); bw.newLine(); }
        } catch (IOException e) {
            System.err.println("Error al guardar " + etiqueta + ": " + e.getMessage());
        }
    }

    private List<String> leer(String ruta, String etiqueta) {
        List<String> lineas = new ArrayList<>();
        Path p = Paths.get(ruta);
        if (!Files.exists(p)) return lineas;
        try (BufferedReader br = Files.newBufferedReader(p, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = br.readLine()) != null)
                if (!linea.trim().isEmpty()) lineas.add(linea);
        } catch (IOException e) {
            System.err.println("Error al cargar " + etiqueta + ": " + e.getMessage());
        }
        return lineas;
    }

    private void invalida(String archivo, String linea, Exception e) {
        lineasInvalidas++;
        System.err.println("Línea inválida omitida en " + archivo + " (" + e.getClass().getSimpleName() + "): " + linea);
    }

    private static String limpio(String s) {
        return s == null ? "" : s.replace("\r", " ").replace("\n", " ").trim();
    }

    // ── DUEÑOS ────────────────────────────────────────────────────────────────
    public void guardarDuenos(List<Dueno> lista) {
        List<String> l = new ArrayList<>();
        for (Dueno d : lista) l.add(d.toLinea());
        escribir(rutaDuenos, l, "dueños");
    }

    public List<Dueno> cargarDuenos() {
        List<Dueno> lista = new ArrayList<>();
        for (String linea : leer(rutaDuenos, "dueños")) {
            try { lista.add(Dueno.fromLinea(linea)); }
            catch (RuntimeException e) { invalida("duenos.txt", linea, e); }
        }
        return lista;
    }

    // ── VETERINARIOS ─────────────────────────────────────────────────────────
    public void guardarVeterinarios(List<Veterinario> lista) {
        List<String> l = new ArrayList<>();
        for (Veterinario v : lista) l.add(v.toLinea());
        escribir(rutaVeterinarios, l, "veterinarios");
    }

    public List<Veterinario> cargarVeterinarios() {
        List<Veterinario> lista = new ArrayList<>();
        for (String linea : leer(rutaVeterinarios, "veterinarios")) {
            try { lista.add(Veterinario.fromLinea(linea)); }
            catch (RuntimeException e) { invalida("veterinarios.txt", linea, e); }
        }
        return lista;
    }

    // ── MASCOTAS ──────────────────────────────────────────────────────────────
    // Formato: id;dniDueño;Especie|nombre|raza|edad|sexo|peso|atributoExtra;observaciones
    public void guardarMascotas(List<Mascota> lista) {
        List<String> l = new ArrayList<>();
        for (Mascota m : lista) {
            Animal a = m.getAnimal();
            String animalLinea;
            if      (a instanceof Perro) animalLinea = ((Perro) a).toLinea();
            else if (a instanceof Gato)  animalLinea = ((Gato)  a).toLinea();
            else                          animalLinea = ((Ave)   a).toLinea();
            l.add(m.getId() + ";" + m.getDueno().getDni() + ";" + animalLinea + ";" + limpio(m.getObservaciones()));
        }
        escribir(rutaMascotas, l, "mascotas");
    }

    public List<Mascota> cargarMascotas(List<Dueno> duenos) {
        List<Mascota> lista = new ArrayList<>();
        for (String linea : leer(rutaMascotas, "mascotas")) {
            try {
                String[] c = linea.split(";", 4);
                int id = Integer.parseInt(c[0].trim());
                String dni = c[1].trim();
                Dueno dueno = null;
                for (Dueno d : duenos) if (d.getDni().equals(dni)) { dueno = d; break; }
                if (dueno == null) throw new IllegalArgumentException("dueño no encontrado: " + dni);
                String[] p = c[2].split("\\|", -1);
                String nombre = p[1], raza = p[2], sexo = p[4], extra = p[6];
                int edad = Integer.parseInt(p[3].trim());
                double peso = Double.parseDouble(p[5].trim());
                Animal animal;
                switch (p[0]) {
                    case "Perro": animal = new Perro(nombre, raza, edad, sexo, peso, extra); break;
                    case "Gato":  animal = new Gato(nombre, raza, edad, sexo, peso, Boolean.parseBoolean(extra)); break;
                    case "Ave":   animal = new Ave(nombre, raza, edad, sexo, peso, extra); break;
                    default: throw new IllegalArgumentException("especie desconocida: " + p[0]);
                }
                lista.add(new Mascota(id, animal, dueno, c.length > 3 ? c[3] : ""));
            } catch (RuntimeException e) { invalida("mascotas.txt", linea, e); }
        }
        return lista;
    }

    // ── CONSULTAS (enlazadas por id de mascota) ───────────────────────────────
    public void guardarConsultas(List<Mascota> lista) {
        List<String> l = new ArrayList<>();
        for (Mascota m : lista)
            for (ConsultaMedica c : m.getHistorial()) l.add(m.getId() + ";" + limpio(c.toLinea()));
        escribir(rutaConsultas, l, "consultas");
    }

    public void cargarConsultasEnMascotas(GestorMascotas gestor) {
        for (String linea : leer(rutaConsultas, "consultas")) {
            try {
                int sep = linea.indexOf(';');
                int idMascota = Integer.parseInt(linea.substring(0, sep).trim());
                Mascota m = gestor.buscarPorId(idMascota);
                if (m == null) throw new IllegalArgumentException("mascota no encontrada: " + idMascota);
                m.agregarConsulta(ConsultaMedica.fromLinea(linea.substring(sep + 1)));
            } catch (RuntimeException e) { invalida("consultas.txt", linea, e); }
        }
    }

    // ── CITAS ─────────────────────────────────────────────────────────────────
    // Formato: id;idMascota;idVeterinario;fecha;hora;estado;motivo;motivoCancelacion
    public void guardarCitas(List<Cita> lista) {
        List<String> l = new ArrayList<>();
        for (Cita c : lista)
            l.add(c.getId() + ";" + c.getMascota().getId() + ";" + c.getVeterinario().getId() + ";"
                    + c.getFecha() + ";" + c.getHora() + ";" + c.getEstado() + ";"
                    + limpio(c.getMotivo()).replace(';', ',') + ";" + limpio(c.getMotivoCancelacion()).replace(';', ','));
        escribir(rutaCitas, l, "citas");
    }

    public List<Cita> cargarCitas(GestorMascotas gestor, List<Veterinario> veterinarios) {
        List<Cita> lista = new ArrayList<>();
        for (String linea : leer(rutaCitas, "citas")) {
            try {
                String[] c = linea.split(";", -1);
                Mascota m = gestor.buscarPorId(Integer.parseInt(c[1].trim()));
                int idVet = Integer.parseInt(c[2].trim());
                Veterinario v = null;
                for (Veterinario x : veterinarios) if (x.getId() == idVet) { v = x; break; }
                if (m == null || v == null) throw new IllegalArgumentException("mascota o veterinario inexistente");
                Cita cita = new Cita(Integer.parseInt(c[0].trim()), m, v, c[3], c[4], c[6]);
                switch (Cita.Estado.valueOf(c[5].trim())) {
                    case ATENDIDA:   cita.confirmarAsistencia(); break;
                    case NO_ASISTIO: cita.marcarNoAsistio(); break;
                    case CANCELADA:  cita.cancelar(c[7]); break;
                    default: break;
                }
                lista.add(cita);
            } catch (RuntimeException e) { invalida("citas.txt", linea, e); }
        }
        return lista;
    }
}
