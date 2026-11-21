import excepciones.DniInvalidoException;
import excepciones.PacienteDuplicadoException;
import excepciones.StockInsuficienteException;
import modelo.Almacen;
import modelo.CitaMedica;
import modelo.Medicamento;
import modelo.Paciente;
import modelo.Persona;
import modelo.PersonaFactory;
import modelo.ProfesionalSalud;
import servicio.SistemaIndoamerica;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final SistemaIndoamerica sistema = new SistemaIndoamerica();
    // Patrón Singleton: garantiza una única instancia compartida del inventario de farmacia
    private static final Almacen almacen = Almacen.getInstancia();

    public static void main(String[] args) {
        cargarDatosIniciales();

        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> registrarPacienteUI();
                case 2 -> registrarProfesionalUI();
                case 3 -> registrarMedicamentoUI();
                case 4 -> gestionarCitaYDispensacionUI();
                case 5 -> mostrarDirectorioEInventarioUI();
                case 6 -> mostrarReportesStreamsUI();
                case 0 -> {
                    System.out.println("\nCerrando sistema del Puesto de Salud Indoamérica. ¡Hasta pronto!");
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("\n[!] Opción no válida. Ingrese un número del 0 al 6.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\n============================================================");
        System.out.println("   PUESTO DE SALUD INDOAMÉRICA - SISTEMA DE ATENCIÓN");
        System.out.println("============================================================");
        System.out.println("1. Registrar Nuevo Paciente");
        System.out.println("2. Registrar Profesional de Salud");
        System.out.println("3. Ingresar Medicamento a Farmacia");
        System.out.println("4. Registrar Consulta Médica y Entrega de Receta");
        System.out.println("5. Consultar Padrón de Personas e Inventario");
        System.out.println("6. Reportes de Gestión y Estadísticas de Farmacia");
        System.out.println("0. Salir del Sistema");
        System.out.println("------------------------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 1: ADMISIÓN DE PACIENTE (Patrón Factory + Excepciones)
    // -------------------------------------------------------------------------
    private static void registrarPacienteUI() {
        System.out.println("\n--- ADMISIÓN DE PACIENTE ---");
        try {
            String dni = leerTexto("DNI (8 dígitos): ");
            String nombre = leerTexto("Nombre completo: ");
            int edad = leerEntero("Edad: ");
            String telefono = leerTexto("Teléfono celular (9 dígitos): ");

            // Patrón Factory: centraliza la creación base del paciente sin acoplar la vista
            Paciente nuevoPaciente = (Paciente) PersonaFactory.crearPersona(
                    "paciente", dni, nombre, edad, telefono);

            String tieneHC = leerTexto("¿El paciente ya cuenta con Historia Clínica y Seguro? (S/N): ");

            if (tieneHC.equalsIgnoreCase("S")) {
                String hc = leerTexto("Número de Historia Clínica (ej. HC-2026-05): ");
                String seguro = leerTexto("Tipo de Seguro (SIS / EsSalud / Particular): ");
                nuevoPaciente.setNumeroHistoriaClinica(hc);
                nuevoPaciente.setTipoSeguro(seguro);
            } else {
                System.out.println("  -> Admisión rápida asignada: Historia [HC-PENDIENTE] | Seguro [SIS].");
            }

            sistema.registrarPaciente(nuevoPaciente);
            System.out.println("[OK] Paciente registrado: " + nuevoPaciente.mostrarResumen());

        } catch (DniInvalidoException | PacienteDuplicadoException | IllegalArgumentException e) {
            System.out.println("[AVISO DE VALIDACIÓN]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 2: REGISTRO DE PROFESIONAL DE SALUD
    // -------------------------------------------------------------------------
    private static void registrarProfesionalUI() {
        System.out.println("\n--- REGISTRO DE PERSONAL DE SALUD ---");
        try {
            String dni = leerTexto("DNI (8 dígitos): ");
            String nombre = leerTexto("Nombre completo: ");
            int edad = leerEntero("Edad: ");
            String telefono = leerTexto("Teléfono celular (9 dígitos): ");
            String especialidad = leerTexto("Especialidad (ej. Medicina General, Enfermería): ");
            String colegiatura = leerTexto("Número de Colegiatura (ej. CMP-54321): ");

            ProfesionalSalud prof = new ProfesionalSalud(dni, nombre, edad, telefono, especialidad, colegiatura);
            sistema.registrarProfesional(prof);
            System.out.println("[OK] Profesional registrado: " + prof.mostrarResumen());

        } catch (DniInvalidoException | IllegalArgumentException e) {
            System.out.println("[AVISO DE VALIDACIÓN]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 3: INGRESO DE MEDICAMENTO A FARMACIA (Singleton)
    // -------------------------------------------------------------------------
    private static void registrarMedicamentoUI() {
        System.out.println("\n--- INGRESO DE MEDICAMENTO A FARMACIA ---");
        try {
            String codigo = leerTexto("Código del medicamento (ej. MED-004): ");
            if (almacen.buscarPorCodigo(codigo) != null) {
                System.out.println("[!] Ya existe un medicamento registrado con el código " + codigo);
                return;
            }
            String nombre = leerTexto("Nombre y concentración (ej. Ibuprofeno 400mg): ");
            int stockActual = leerEntero("Cantidad de unidades que ingresan: ");
            int stockMinimo = leerEntero("Cantidad mínima para alerta de reposición: ");

            Medicamento med = new Medicamento(codigo, nombre, stockActual, stockMinimo);
            almacen.agregarMedicamento(med);
            System.out.println("[OK] Medicamento agregado al inventario de farmacia.");

        } catch (IllegalArgumentException e) {
            System.out.println("[AVISO]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 4: CONSULTA MÉDICA Y DISPENSACIÓN (Composición + StockInsuficiente)
    // -------------------------------------------------------------------------
    private static void gestionarCitaYDispensacionUI() {
        System.out.println("\n--- NUEVA CONSULTA MÉDICA Y ENTREGA DE RECETA ---");
        if (sistema.getPacientes().isEmpty() || sistema.getProfesionales().isEmpty()) {
            System.out.println("[!] Debe haber al menos un paciente y un profesional registrados.");
            return;
        }

        try {
            String codigoCita = leerTexto("Código de atención (ej. CITA-001): ");
            String fecha = leerTexto("Fecha (AAAA-MM-DD): ");
            String hora = leerTexto("Hora (HH:MM): ");
            String motivo = leerTexto("Motivo de consulta o diagnóstico: ");

            // Selección de Paciente (numerado desde 1 para el usuario)
            System.out.println("\nPacientes registrados:");
            List<Paciente> pacientes = sistema.getPacientes();
            for (int i = 0; i < pacientes.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + pacientes.get(i).getNombre()
                        + " (DNI: " + pacientes.get(i).obtenerDniEnmascarado() + ")");
            }
            int numPac = leerEntero("Seleccione el número del paciente: ");
            if (numPac < 1 || numPac > pacientes.size()) {
                System.out.println("[!] Número de paciente no válido.");
                return;
            }

            // Selección de Profesional (numerado desde 1 para el usuario)
            System.out.println("\nProfesionales de turno:");
            List<ProfesionalSalud> profesionales = sistema.getProfesionales();
            for (int i = 0; i < profesionales.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + profesionales.get(i).getNombre()
                        + " - " + profesionales.get(i).getEspecialidad());
            }
            int numProf = leerEntero("Seleccione el número del profesional: ");
            if (numProf < 1 || numProf > profesionales.size()) {
                System.out.println("[!] Número de profesional no válido.");
                return;
            }

            CitaMedica cita = new CitaMedica(
                    codigoCita, fecha, hora, motivo, pacientes.get(numPac - 1), profesionales.get(numProf - 1));
            sistema.registrarCita(cita);

            // Entrega de medicamentos recetados
            String continuar = leerTexto("¿Entregar medicamentos recetados en esta consulta? (S/N): ");
            while (continuar.equalsIgnoreCase("S")) {
                System.out.println("\n  Medicamentos disponibles en farmacia:");
                for (Medicamento m : almacen.getMedicamentos()) {
                    System.out.println("   * [" + m.getCodigo() + "] " + m.getNombre()
                            + " (Disponible: " + m.getStockActual() + ")");
                }

                String codMed = leerTexto("  Ingrese código del medicamento (ej. MED-001): ");
                Medicamento med = almacen.buscarPorCodigo(codMed);

                if (med == null) {
                    System.out.println("  [!] No se encontró medicamento con código: " + codMed);
                } else {
                    int cantidad = leerEntero("  Cantidad de unidades a entregar de " + med.getNombre() + ": ");
                    try {
                        sistema.dispensarMedicamentoEnCita(cita, med, cantidad);
                        System.out.println("  [OK] Entrega registrada. Stock restante: " + med.getStockActual());
                    } catch (StockInsuficienteException | IllegalArgumentException e) {
                        System.out.println("  [ALERTA DE FARMACIA]: " + e.getMessage());
                    }
                }
                continuar = leerTexto("¿Agregar otro medicamento a la receta? (S/N): ");
            }

            System.out.println("\n--- CONSTANCIA DE ATENCIÓN REGISTRADA ---");
            System.out.println(cita.mostrarResumenCita());

        } catch (IllegalArgumentException e) {
            System.out.println("[AVISO EN DATOS DE ATENCIÓN]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 5: PADRÓN E INVENTARIO (Polimorfismo en Persona)
    // -------------------------------------------------------------------------
    private static void mostrarDirectorioEInventarioUI() {
        System.out.println("\n--- PADRÓN DE PACIENTES Y PERSONAL DE SALUD ---");
        // Polimorfismo: invocación dinámica de mostrarResumen() sobre la clase base Persona
        List<Persona> todos = new ArrayList<>();
        todos.addAll(sistema.getPacientes());
        todos.addAll(sistema.getProfesionales());

        for (Persona p : todos) {
            System.out.println("  * " + p.mostrarResumen());
        }

        System.out.println("\n--- INVENTARIO ACTUAL DE FARMACIA ---");
        for (Medicamento m : almacen.getMedicamentos()) {
            String alerta = SistemaIndoamerica.evaluarNivelAlertaStock(m.getStockActual(), m.getStockMinimo());
            System.out.printf("  [%s] %-18s | Disponible: %2d (Mínimo: %2d) | Estado: %s%n",
                    m.getCodigo(), m.getNombre(), m.getStockActual(), m.getStockMinimo(), alerta);
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 6: REPORTES Y ESTADÍSTICAS (Streams, Lambdas y Funciones Puras)
    // -------------------------------------------------------------------------
    private static void mostrarReportesStreamsUI() {
        System.out.println("\n--- REPORTES DE GESTIÓN Y FARMACIA ---");

        // Consultas declarativas con Streams
        System.out.println("1. Medicamentos por agotarse (stock crítico) : " + sistema.obtenerNombresStockCritico());
        System.out.println("2. Total de unidades entregadas en consultas : " + sistema.calcularTotalUnidadesDispensadas());
        System.out.println("3. Especialidades médicas activas            : " + sistema.obtenerEspecialidadesUnicas());
        System.out.println("4. Existencias actuales por código           : " + sistema.obtenerMapaStockPorCodigo());

        // Filtro por tipo de seguro
        String seguro = leerTexto("\nConsultar pacientes por tipo de seguro (ej. SIS, EsSalud): ");
        List<String> asegurados = sistema.listarResumenesPorSeguro(seguro);
        System.out.println("   Total de afiliados a '" + seguro + "': " + asegurados.size());
        asegurados.forEach(resumen -> System.out.println("    -> " + resumen));

        // Filtro por edad (Orden superior con Predicate)
        int edadMinima = leerEntero("\nConsultar pacientes a partir de qué edad (ej. 60): ");
        List<Paciente> filtrados = sistema.filtrarPacientesPorCriterio(p -> p.getEdad() >= edadMinima);
        System.out.println("   Pacientes de " + edadMinima + " años a más: " + filtrados.size());
        filtrados.forEach(p -> System.out.println("    -> " + p.getNombre() + " (" + p.getEdad() + " años)"));

        // Función pura: proyección de abastecimiento sin mutar el inventario actual
        List<Integer> consumos = almacen.getMedicamentos().stream()
                .map(Medicamento::getStockActual)
                .toList();
        System.out.println("\n5. Estimación de abastecimiento (+25% para próximo mes): "
                + SistemaIndoamerica.proyectarDemandaMensual(consumos, 0.25));
    }

    // -------------------------------------------------------------------------
    // UTILIDADES DE LECTURA SEGURA Y PRECARGA DE DATOS FICTICIOS
    // -------------------------------------------------------------------------
    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("[!] Por favor, ingrese un número válido.");
            }
        }
    }

    private static void cargarDatosIniciales() {
        try {
            almacen.agregarMedicamento(new Medicamento("MED-001", "Paracetamol 500mg", 50, 15));
            almacen.agregarMedicamento(new Medicamento("MED-002", "Amoxicilina 500mg", 14, 15));
            almacen.agregarMedicamento(new Medicamento("MED-003", "Ibuprofeno 400mg", 5, 10));

            sistema.registrarPaciente(new Paciente("45678912", "Juan Carlos Pérez", 68, "944112233", "HC-2026-01", "SIS"));
            sistema.registrarPaciente(new Paciente("78945612", "Lucía Mendoza", 25, "955667788", "HC-2026-02", "EsSalud"));

            sistema.registrarProfesional(new ProfesionalSalud("18902345", "Dra. Elena Rojas", 41, "949887766", "Medicina General", "CMP-54321"));
            sistema.registrarProfesional(new ProfesionalSalud("19203456", "Lic. Carlos Vargas", 36, "948112233", "Enfermería", "CEP-98765"));
        } catch (Exception e) {
            System.out.println("[Error en carga inicial]: " + e.getMessage());
        }
    }
}