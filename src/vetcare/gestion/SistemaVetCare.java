package vetcare.gestion;

import vetcare.modelo.*;
import vetcare.util.PersistenciaArchivos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SistemaVetCare {

    private final GestorMascotas     gestorMascotas;
    private final GestorCitas        gestorCitas;
    private final ArrayList<Dueno>   duenos;
    private final ArrayList<Veterinario> veterinarios;
    private final PersistenciaArchivos persistencia;
    private final Scanner            sc;

    // Credenciales de acceso básicas
    private static final String USUARIO_ADMIN = "admin";
    private static final String CLAVE_ADMIN   = "vetcare2026";

    public SistemaVetCare(String carpetaData) {
        gestorMascotas = new GestorMascotas();
        gestorCitas    = new GestorCitas();
        duenos         = new ArrayList<>();
        veterinarios   = new ArrayList<>();
        persistencia   = new PersistenciaArchivos(carpetaData);
        sc             = new Scanner(System.in);
    }

    // ── INICIO DEL SISTEMA ───────────────────────────────────────────────────
    public void iniciar() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       🐾  VETCARE — Sistema v1.0     ║");
        System.out.println("╚══════════════════════════════════════╝");

        if (!autenticar()) {
            System.out.println("Acceso denegado. Cerrando el sistema.");
            return;
        }
        cargarDatos();
        boolean cerrarSesion;
        do {
            cerrarSesion = menuPrincipal();
            if (cerrarSesion) {
                guardarDatos();
                System.out.println("\nSesión cerrada. Datos guardados.");
                if (!autenticar()) {
                    System.out.println("Acceso denegado. Cerrando el sistema.");
                    return;
                }
            }
        } while (cerrarSesion);
        guardarDatos();
        System.out.println("\nDatos guardados. ¡Hasta luego!");
    }

    // ── AUTENTICACIÓN ────────────────────────────────────────────────────────
    private boolean autenticar() {
        int intentos = 3;
        while (intentos-- > 0) {
            System.out.print("\nUsuario: ");
            String usuario = sc.nextLine().trim();
            System.out.print("Contraseña: ");
            String clave = sc.nextLine().trim();
            if (USUARIO_ADMIN.equals(usuario) && CLAVE_ADMIN.equals(clave)) {
                System.out.println("Acceso concedido. Bienvenido/a, " + usuario + ".");
                return true;
            }
            System.out.println("Usuario o contraseña inválidos. Intentos restantes: " + intentos);
        }
        return false;
    }

    // ── MENÚ PRINCIPAL ───────────────────────────────────────────────────────
    private boolean menuPrincipal() {
        int opcion;
        boolean cerrarSesion = false;
        do {
            System.out.println("\n┌─────────────────────────────────────┐");
            System.out.println("│         MENÚ PRINCIPAL               │");
            System.out.println("├─────────────────────────────────────┤");
            System.out.println("│  1. Gestión de Mascotas              │");
            System.out.println("│  2. Gestión de Dueños                │");
            System.out.println("│  3. Gestión de Veterinarios          │");
            System.out.println("│  4. Gestión de Citas                 │");
            System.out.println("│  5. Historial Médico                 │");
            System.out.println("│  6. Citas del día                    │");
            System.out.println("│  7. Cerrar sesión                    │");
            System.out.println("│  0. Guardar y salir                  │");
            System.out.println("└─────────────────────────────────────┘");
            System.out.print("Opción: ");
            opcion = leerInt();
            switch (opcion) {
                case 1 -> menuMascotas();
                case 2 -> menuDuenos();
                case 3 -> menuVeterinarios();
                case 4 -> menuCitas();
                case 5 -> menuHistorial();
                case 6 -> mostrarCitasDelDia();
                case 7 -> cerrarSesion = true;
                case 0 -> System.out.println("Cerrando...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0 && !cerrarSesion);
        return cerrarSesion;
    }

    // ── MENÚ MASCOTAS ─────────────────────────────────────────────────────────
    private void menuMascotas() {
        int op;
        do {
            System.out.println("\n── Mascotas ──");
            System.out.println("1. Registrar mascota");
            System.out.println("2. Listar mascotas");
            System.out.println("3. Buscar por nombre");
            System.out.println("4. Filtrar por especie");
            System.out.println("5. Eliminar mascota");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            op = leerInt();
            switch (op) {
                case 1 -> registrarMascota();
                case 2 -> gestorMascotas.listar();
                case 3 -> buscarMascotaPorNombre();
                case 4 -> filtrarPorEspecie();
                case 5 -> eliminarMascota();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    private void registrarMascota() {
        System.out.println("\n── Registrar Mascota ──");
        if (duenos.isEmpty()) { System.out.println("Registre un dueño primero."); return; }

        System.out.print("Especie (1-Perro / 2-Gato / 3-Ave): ");
        int esp = leerInt();
        System.out.print("Nombre de la mascota: ");
        String nombre = sc.nextLine().trim();
        System.out.print("Raza: ");
        String raza = sc.nextLine().trim();
        System.out.print("Edad (años): ");
        int edad = leerInt();
        System.out.print("Sexo (Macho/Hembra): ");
        String sexo = sc.nextLine().trim();
        System.out.print("Peso (kg): ");
        double peso = leerDouble();

        Animal animal;
        try {
            switch (esp) {
                case 1 -> {
                    System.out.print("Tamaño (Pequeño/Mediano/Grande): ");
                    animal = new Perro(nombre, raza, edad, sexo, peso, sc.nextLine().trim());
                }
                case 2 -> {
                    System.out.print("¿Castrado? (s/n): ");
                    animal = new Gato(nombre, raza, edad, sexo, peso,
                            sc.nextLine().trim().equalsIgnoreCase("s"));
                }
                case 3 -> {
                    System.out.print("Tipo de pico (Curvo/Recto/Ganchudo): ");
                    animal = new Ave(nombre, raza, edad, sexo, peso, sc.nextLine().trim());
                }
                default -> { System.out.println("Especie no válida."); return; }
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        Dueno dueno = seleccionarDueno();
        if (dueno == null) return;

        System.out.print("Observaciones (alergias, condiciones especiales): ");
        String obs = sc.nextLine().trim();

        Mascota mascota = new Mascota(gestorMascotas.getSiguienteId(), animal, dueno, obs);
        gestorMascotas.agregar(mascota);
        System.out.println("Mascota registrada: " + mascota);
    }

    private void buscarMascotaPorNombre() {
        System.out.print("Nombre a buscar: ");
        String nombre = sc.nextLine().trim();
        var resultado = gestorMascotas.buscarPorNombre(nombre);
        if (resultado.isEmpty()) System.out.println("No se encontraron mascotas.");
        else resultado.forEach(m -> System.out.println("  " + m));
    }

    private void filtrarPorEspecie() {
        System.out.print("Especie (Perro/Gato/Ave): ");
        String esp = sc.nextLine().trim();
        var resultado = gestorMascotas.filtrarPorEspecie(esp);
        if (resultado.isEmpty()) System.out.println("No hay mascotas de esa especie.");
        else resultado.forEach(m -> System.out.println("  " + m));
    }

    private void eliminarMascota() {
        System.out.print("ID de la mascota a eliminar: ");
        int id = leerInt();
        if (gestorMascotas.eliminar(id)) System.out.println("Mascota eliminada.");
        else System.out.println("Mascota no encontrada.");
    }

    // ── MENÚ DUEÑOS ──────────────────────────────────────────────────────────
    private void menuDuenos() {
        int op;
        do {
            System.out.println("\n── Dueños ──");
            System.out.println("1. Registrar dueño");
            System.out.println("2. Listar dueños");
            System.out.println("3. Buscar por DNI");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            op = leerInt();
            switch (op) {
                case 1 -> registrarDueno();
                case 2 -> duenos.forEach(d -> System.out.println("  " + d));
                case 3 -> buscarDuenoPorDni();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    private void registrarDueno() {
        System.out.println("\n── Registrar Dueño ──");
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();
        System.out.print("DNI: ");
        String dni = sc.nextLine().trim();

        for (Dueno d : duenos)
            if (d.getDni().equals(dni)) {
                System.out.println("Ya existe un dueño con ese DNI."); return;
            }

        System.out.print("Teléfono: ");
        String tel = sc.nextLine().trim();
        System.out.print("Correo electrónico: ");
        String correo = sc.nextLine().trim();
        System.out.print("Dirección: ");
        String dir = sc.nextLine().trim();

        int nuevoId = duenos.isEmpty() ? 1 : duenos.get(duenos.size() - 1).getId() + 1;
        try {
            Dueno d = new Dueno(nuevoId, nombre, dni, tel, correo, dir);
            duenos.add(d);
            System.out.println("Dueño registrado: " + d);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void buscarDuenoPorDni() {
        System.out.print("DNI a buscar: ");
        String dni = sc.nextLine().trim();
        for (Dueno d : duenos) {
            if (d.getDni().equals(dni)) {
                System.out.println("  " + d);
                var mascotas = gestorMascotas.buscarPorDuenoDni(dni);
                if (mascotas.isEmpty()) System.out.println("  Sin mascotas registradas.");
                else mascotas.forEach(m -> System.out.println("    → " + m));
                return;
            }
        }
        System.out.println("Dueño no encontrado. ¿Desea registrarlo? (use la opción 1)");
    }

    private Dueno seleccionarDueno() {
        System.out.print("DNI del dueño: ");
        String dni = sc.nextLine().trim();
        for (Dueno d : duenos)
            if (d.getDni().equals(dni)) return d;
        System.out.println("Dueño no encontrado.");
        return null;
    }

    // ── MENÚ VETERINARIOS ─────────────────────────────────────────────────────
    private void menuVeterinarios() {
        int op;
        do {
            System.out.println("\n── Veterinarios ──");
            System.out.println("1. Registrar veterinario");
            System.out.println("2. Listar veterinarios activos");
            System.out.println("3. Ver agenda de un veterinario");
            System.out.println("4. Desactivar veterinario");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            op = leerInt();
            switch (op) {
                case 1 -> registrarVeterinario();
                case 2 -> veterinarios.stream()
                        .filter(Veterinario::isActivo)
                        .forEach(v -> System.out.println("  " + v));
                case 3 -> verAgendaVeterinario();
                case 4 -> desactivarVeterinario();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    private void registrarVeterinario() {
        System.out.println("\n── Registrar Veterinario ──");
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine().trim();
        System.out.print("DNI: ");
        String dni = sc.nextLine().trim();
        System.out.print("Teléfono: ");
        String tel = sc.nextLine().trim();
        System.out.print("Correo: ");
        String correo = sc.nextLine().trim();
        System.out.print("Especialidad: ");
        String esp = sc.nextLine().trim();
        System.out.print("N.° de colegiatura: ");
        String col = sc.nextLine().trim();

        int nuevoId = veterinarios.isEmpty() ? 1 : veterinarios.get(veterinarios.size() - 1).getId() + 1;
        try {
            Veterinario v = new Veterinario(nuevoId, nombre, dni, tel, correo, esp, col);
            veterinarios.add(v);
            System.out.println("Veterinario registrado: " + v);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void verAgendaVeterinario() {
        System.out.print("ID del veterinario: ");
        int id = leerInt();
        var agenda = gestorCitas.agendaVeterinario(id);
        if (agenda.isEmpty()) System.out.println("Este veterinario no tiene citas programadas.");
        else agenda.forEach(c -> System.out.println("  " + c));
    }

    private void desactivarVeterinario() {
        System.out.print("ID del veterinario a desactivar: ");
        int id = leerInt();
        for (Veterinario v : veterinarios)
            if (v.getId() == id) { v.desactivar(); System.out.println("Veterinario desactivado."); return; }
        System.out.println("Veterinario no encontrado.");
    }

    // ── MENÚ CITAS ────────────────────────────────────────────────────────────
    private void menuCitas() {
        int op;
        do {
            System.out.println("\n── Citas ──");
            System.out.println("1. Agendar cita");
            System.out.println("2. Listar citas pendientes");
            System.out.println("3. Confirmar asistencia");
            System.out.println("4. Cancelar cita");
            System.out.println("5. Citas por fecha");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            op = leerInt();
            switch (op) {
                case 1 -> agendarCita();
                case 2 -> gestorCitas.pendientes().forEach(c -> System.out.println("  " + c));
                case 3 -> confirmarCita();
                case 4 -> cancelarCita();
                case 5 -> citasPorFecha();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    private void agendarCita() {
        System.out.println("\n── Agendar Cita ──");
        if (gestorMascotas.getTodas().isEmpty()) { System.out.println("No hay mascotas registradas."); return; }
        if (veterinarios.isEmpty()) { System.out.println("No hay veterinarios registrados."); return; }

        System.out.print("ID de la mascota: ");
        int idM = leerInt();
        Mascota m = gestorMascotas.buscarPorId(idM);
        if (m == null) { System.out.println("Mascota no encontrada."); return; }

        System.out.print("ID del veterinario: ");
        int idV = leerInt();
        Veterinario v = null;
        for (Veterinario vet : veterinarios)
            if (vet.getId() == idV) { v = vet; break; }
        if (v == null || !v.isActivo()) { System.out.println("Veterinario no encontrado o inactivo."); return; }

        System.out.print("Fecha (dd/MM/yyyy): ");
        String fecha = sc.nextLine().trim();
        System.out.print("Hora (HH:mm): ");
        String hora = sc.nextLine().trim();

        if (gestorCitas.hayConflicto(fecha, hora, idV)) {
            System.out.println("Conflicto: el veterinario ya tiene cita en ese horario.");
            return;
        }

        System.out.print("Motivo de la consulta: ");
        String motivo = sc.nextLine().trim();

        Cita cita = new Cita(gestorCitas.getSiguienteId(), m, v, fecha, hora, motivo);
        gestorCitas.agregar(cita);
        System.out.println("Cita agendada: " + cita);
    }

    private void confirmarCita() {
        System.out.print("ID de la cita a confirmar: ");
        int id = leerInt();
        Cita c = gestorCitas.buscarPorId(id);
        if (c == null) { System.out.println("Cita no encontrada."); return; }
        try {
            System.out.println("¿Asistió? (s=Atendida / n=No asistió): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) c.confirmarAsistencia();
            else c.marcarNoAsistio();
            System.out.println("Estado actualizado: " + c.getEstado());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void cancelarCita() {
        System.out.print("ID de la cita a cancelar: ");
        int id = leerInt();
        Cita c = gestorCitas.buscarPorId(id);
        if (c == null) { System.out.println("Cita no encontrada."); return; }
        System.out.print("Motivo de cancelación: ");
        try {
            c.cancelar(sc.nextLine().trim());
            System.out.println("Cita cancelada.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void citasPorFecha() {
        System.out.print("Fecha (dd/MM/yyyy): ");
        String fecha = sc.nextLine().trim();
        var lista = gestorCitas.porFecha(fecha);
        if (lista.isEmpty()) System.out.println("No hay citas para esa fecha.");
        else lista.forEach(c -> System.out.println("  " + c));
    }

    private void mostrarCitasDelDia() {
        var hoy = gestorCitas.citasDelDia();
        System.out.println("\n── Citas de hoy ──");
        if (hoy.isEmpty()) System.out.println("No hay citas programadas para hoy.");
        else hoy.forEach(c -> System.out.println("  " + c));
    }

    // ── MENÚ HISTORIAL ────────────────────────────────────────────────────────
    private void menuHistorial() {
        int op;
        do {
            System.out.println("\n── Historial Médico ──");
            System.out.println("1. Ver historial de una mascota");
            System.out.println("2. Registrar consulta");
            System.out.println("0. Volver");
            System.out.print("Opción: ");
            op = leerInt();
            switch (op) {
                case 1 -> verHistorial();
                case 2 -> registrarConsulta();
                case 0 -> {}
                default -> System.out.println("Opción no válida.");
            }
        } while (op != 0);
    }

    private void verHistorial() {
        System.out.print("ID de la mascota: ");
        int id = leerInt();
        Mascota m = gestorMascotas.buscarPorId(id);
        if (m == null) { System.out.println("Mascota no encontrada."); return; }
        System.out.println("Historial de: " + m.getAnimal().getNombre());
        m.mostrarHistorial();
    }

    private void registrarConsulta() {
        System.out.print("ID de la mascota: ");
        int id = leerInt();
        Mascota m = gestorMascotas.buscarPorId(id);
        if (m == null) { System.out.println("Mascota no encontrada."); return; }

        int nuevoId = m.getHistorial().size() + 1;
        System.out.print("Fecha (dd/MM/yyyy): ");
        String fecha = sc.nextLine().trim();
        System.out.print("Diagnóstico: ");
        String diag = sc.nextLine().trim();
        System.out.print("Tratamiento (Enter para omitir): ");
        String trat = sc.nextLine().trim();
        System.out.print("Observaciones (Enter para omitir): ");
        String obs = sc.nextLine().trim();

        ConsultaMedica consulta;
        if (obs.isEmpty() && trat.isEmpty())
            consulta = new ConsultaMedica(nuevoId, fecha, diag);
        else if (obs.isEmpty())
            consulta = new ConsultaMedica(nuevoId, fecha, diag, trat);
        else
            consulta = new ConsultaMedica(nuevoId, fecha, diag, trat, obs);

        m.agregarConsulta(consulta);
        System.out.println("Consulta registrada.");
    }

    // ── PERSISTENCIA ─────────────────────────────────────────────────────────
    private void guardarDatos() {
        persistencia.guardarDuenos(duenos);
        persistencia.guardarVeterinarios(veterinarios);
        persistencia.guardarMascotas(gestorMascotas.getTodas());
        persistencia.guardarConsultas(gestorMascotas.getTodas());
        persistencia.guardarCitas(gestorCitas.getTodas());
    }

    private void cargarDatos() {
        duenos.addAll(persistencia.cargarDuenos());
        veterinarios.addAll(persistencia.cargarVeterinarios());
        for (Mascota m : persistencia.cargarMascotas(duenos))
            gestorMascotas.agregar(m);
        persistencia.cargarConsultasEnMascotas(gestorMascotas);
        for (Cita c : persistencia.cargarCitas(gestorMascotas, veterinarios))
            gestorCitas.agregar(c);
        int invalidas = persistencia.getLineasInvalidas();
        if (invalidas > 0)
            System.out.println("Aviso: se omitieron " + invalidas + " línea(s) inválida(s) al cargar los archivos.");
    }

    // ── UTILIDADES ───────────────────────────────────────────────────────────
    private int leerInt() {
        while (true) {
            try {
                int val = Integer.parseInt(sc.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }

    private double leerDouble() {
        while (true) {
            try {
                double val = Double.parseDouble(sc.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número decimal válido: ");
            }
        }
    }
}
