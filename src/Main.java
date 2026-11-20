
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
    private static final Almacen almacen = Almacen.getInstancia(); // Singleton

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
                case 0 -> System.out.println("\nCerrando Sistema Rural Indoamérica. ¡Hasta pronto!");
                default -> System.out.println("\n[!] Opción no válida. Intente nuevamente.");
            }
        }
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n============================================================");
        System.out.println("   PUESTO DE SALUD INDOAMÉRICA - MENÚ PRINCIPAL");
        System.out.println("============================================================");
        System.out.println("1. Registrar Paciente (Manual o vía PersonaFactory)");
        System.out.println("2. Registrar Profesional de Salud");
        System.out.println("3. Agregar Medicamento al Almacén");
        System.out.println("4. Registrar Cita Médica y Dispensar Medicamentos");
        System.out.println("5. Ver Directorio (Polimorfismo) e Inventario");
        System.out.println("6. Reportes Analíticos (Lambdas, Funciones Puras y Streams)");
        System.out.println("0. Salir");
        System.out.println("------------------------------------------------------------");
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 1: REGISTRO DE PACIENTE (Factory / Constructor + Excepciones)
    // -------------------------------------------------------------------------
    private static void registrarPacienteUI() {
        System.out.println("\n--- REGISTRO DE PACIENTE ---");
        try {
            String dni = leerTexto("DNI (8 dígitos): ");
            String nombre = leerTexto("Nombre completo: ");
            int edad = leerEntero("Edad: ");
            String telefono = leerTexto("Teléfono (9 dígitos): ");
            String modo = leerTexto("¿Usar valores por defecto de PersonaFactory (HC-PENDIENTE / SIS)? (S/N): ");

            Paciente nuevoPaciente;
            if (modo.equalsIgnoreCase("S")) {
                Persona p = PersonaFactory.crearPersona("paciente", dni, nombre, edad, telefono);
                nuevoPaciente = (Paciente) p;
            } else {
                String hc = leerTexto("Número de Historia Clínica: ");
                String seguro = leerTexto("Tipo de Seguro (ej. SIS, EsSalud): ");
                nuevoPaciente = new Paciente(dni, nombre, edad, telefono, hc, seguro);
            }

            sistema.registrarPaciente(nuevoPaciente);
            System.out.println("[OK] Paciente registrado: " + nuevoPaciente.mostrarResumen());

        } catch (DniInvalidoException | PacienteDuplicadoException | IllegalArgumentException e) {
            System.out.println("[ERROR DE VALIDACIÓN]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 2: REGISTRO DE PROFESIONAL DE SALUD
    // -------------------------------------------------------------------------
    private static void registrarProfesionalUI() {
        System.out.println("\n--- REGISTRO DE PROFESIONAL DE SALUD ---");
        try {
            String dni = leerTexto("DNI (8 dígitos): ");
            String nombre = leerTexto("Nombre completo: ");
            int edad = leerEntero("Edad: ");
            String telefono = leerTexto("Teléfono (9 dígitos): ");
            String especialidad = leerTexto("Especialidad: ");
            String colegiatura = leerTexto("Número de Colegiatura: ");

            ProfesionalSalud prof = new ProfesionalSalud(dni, nombre, edad, telefono, especialidad, colegiatura);
            sistema.registrarProfesional(prof);
            System.out.println("[OK] Profesional registrado: " + prof.mostrarResumen());

        } catch (DniInvalidoException | IllegalArgumentException e) {
            System.out.println("[ERROR DE VALIDACIÓN]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 3: AGREGAR MEDICAMENTO AL ALMACÉN (Singleton)
    // -------------------------------------------------------------------------
    private static void registrarMedicamentoUI() {
        System.out.println("\n--- INGRESO DE MEDICAMENTO AL ALMACÉN ---");
        try {
            String codigo = leerTexto("Código (ej. MED-001): ");
            if (almacen.buscarPorCodigo(codigo) != null) {
                System.out.println("[!] Ya existe un medicamento con el código " + codigo);
                return;
            }
            String nombre = leerTexto("Nombre del medicamento: ");
            int stockActual = leerEntero("Stock inicial: ");
            int stockMinimo = leerEntero("Stock mínimo de alerta: ");

            Medicamento med = new Medicamento(codigo, nombre, stockActual, stockMinimo);
            almacen.agregarMedicamento(med);
            System.out.println("[OK] Medicamento agregado al inventario.");

        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 4: CITA MÉDICA Y DISPENSACIÓN (Composición + StockInsuficiente)
    // -------------------------------------------------------------------------
    private static void gestionarCitaYDispensacionUI() {
        System.out.println("\n--- NUEVA CITA MÉDICA Y DISPENSACIÓN ---");
        if (sistema.getPacientes().isEmpty() || sistema.getProfesionales().isEmpty()) {
            System.out.println("[!] Debe haber al menos un paciente y un profesional registrados.");
            return;
        }

        try {
            String codigoCita = leerTexto("Código de cita (ej. CITA-002): ");
            String fecha = leerTexto("Fecha (YYYY-MM-DD): ");
            String hora = leerTexto("Hora (HH:MM): ");
            String motivo = leerTexto("Motivo de consulta: ");

            // Selección de Paciente
            System.out.println("\nPacientes disponibles:");
            List<Paciente> pacientes = sistema.getPacientes();
            for (int i = 0; i < pacientes.size(); i++) {
                System.out.println("  [" + i + "] " + pacientes.get(i).getNombre()
                        + " (DNI: " + pacientes.get(i).obtenerDniEnmascarado() + ")");
            }
            int idxPac = leerEntero("Seleccione índice del paciente: ");
            if (idxPac < 0 || idxPac >= pacientes.size()) {
                System.out.println("[!] Índice de paciente fuera de rango.");
                return;
            }

            // Selección de Profesional
            System.out.println("\nProfesionales disponibles:");
            List<ProfesionalSalud> profesionales = sistema.getProfesionales();
            for (int i = 0; i < profesionales.size(); i++) {
                System.out.println("  [" + i + "] " + profesionales.get(i).getNombre()
                        + " - " + profesionales.get(i).getEspecialidad());
            }
            int idxProf = leerEntero("Seleccione índice del profesional: ");
            if (idxProf < 0 || idxProf >= profesionales.size()) {
                System.out.println("[!] Índice de profesional fuera de rango.");
                return;
            }

            CitaMedica cita = new CitaMedica(
                    codigoCita, fecha, hora, motivo, pacientes.get(idxPac), profesionales.get(idxProf));
            sistema.registrarCita(cita);

            // Bucle para dispensar medicamentos en la receta de la cita
            String continuar = leerTexto("¿Desea dispensar medicamentos en esta cita? (S/N): ");
            while (continuar.equalsIgnoreCase("S")) {
                String codMed = leerTexto("Ingrese código del medicamento (ej. MED-001): ");
                Medicamento med = almacen.buscarPorCodigo(codMed);

                if (med == null) {
                    System.out.println("[!] No se encontró medicamento con código: " + codMed);
                } else {
                    System.out.println("    Seleccionado: " + med.getNombre() + " | Stock disponible: " + med.getStockActual());
                    int cantidad = leerEntero("    Cantidad a entregar: ");
                    try {
                        sistema.dispensarMedicamentoEnCita(cita, med, cantidad);
                        System.out.println("    [OK] Dispensado. Nuevo stock: " + med.getStockActual());
                    } catch (StockInsuficienteException | IllegalArgumentException e) {
                        System.out.println("    [ALERTA DE STOCK]: " + e.getMessage());
                    }
                }
                continuar = leerTexto("¿Agregar otro medicamento a esta cita? (S/N): ");
            }

            System.out.println("\n--- RESUMEN DE LA CITA REGISTRADA ---");
            System.out.println(cita.mostrarResumenCita());

        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR EN DATOS DE CITA]: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 5: POLIMORFISMO E INVENTARIO
    // -------------------------------------------------------------------------
    private static void mostrarDirectorioEInventarioUI() {
        System.out.println("\n--- DIRECTORIO GENERAL (POLIMORFISMO EN PERSONA) ---");
        List<Persona> todos = new ArrayList<>();
        todos.addAll(sistema.getPacientes());
        todos.addAll(sistema.getProfesionales());

        for (Persona p : todos) {
            System.out.println("  * " + p.mostrarResumen());
        }

        System.out.println("\n--- INVENTARIO ACTUAL DEL ALMACÉN ---");
        for (Medicamento m : almacen.getMedicamentos()) {
            String alerta = SistemaIndoamerica.evaluarNivelAlertaStock(m.getStockActual(), m.getStockMinimo());
            System.out.printf("  [%s] %-18s | Stock: %2d (Mín: %2d) | Estado: %s%n",
                    m.getCodigo(), m.getNombre(), m.getStockActual(), m.getStockMinimo(), alerta);
        }
    }

    // -------------------------------------------------------------------------
    // OPCIÓN 6: STREAMS, LAMBDAS Y FUNCIONES PURAS
    // -------------------------------------------------------------------------
    private static void mostrarReportesStreamsUI() {
        System.out.println("\n--- REPORTES ANALÍTICOS (STREAMS & LAMBDAS) ---");

        // 1. Streams: Stock crítico
        System.out.println("1. Medicamentos en stock crítico (<= mínimo): " + sistema.obtenerNombresStockCritico());

        // 2. Streams: Total unidades dispensadas (flatMap + map + reduce)
        System.out.println("2. Total unidades dispensadas en citas      : " + sistema.calcularTotalUnidadesDispensadas());

        // 3. Streams: Especialidades únicas (Set)
        System.out.println("3. Especialidades médicas únicas (Set)      : " + sistema.obtenerEspecialidadesUnicas());

        // 4. Streams: Mapa de stock (Map)
        System.out.println("4. Mapa Código -> Stock (Map)               : " + sistema.obtenerMapaStockPorCodigo());

        // 5. Streams: Filtro por seguro
        String seguro = leerTexto("\nIngrese tipo de seguro a consultar (ej. SIS, EsSalud): ");
        List<String> asegurados = sistema.listarResumenesPorSeguro(seguro);
        System.out.println("   Pacientes encontrados con seguro '" + seguro + "': " + asegurados.size());
        asegurados.forEach(resumen -> System.out.println("    -> " + resumen));

        // 6. Orden superior con Predicate: Adultos mayores
        int edadMinima = leerEntero("\nFiltrar pacientes mayores o iguales a edad: ");
        List<Paciente> filtrados = sistema.filtrarPacientesPorCriterio(p -> p.getEdad() >= edadMinima);
        System.out.println("   Pacientes con edad >= " + edadMinima + ": " + filtrados.size());
        filtrados.forEach(p -> System.out.println("    -> " + p.getNombre() + " (" + p.getEdad() + " años)"));

        // 7. Función pura: Proyección de demanda
        List<Integer> consumos = almacen.getMedicamentos().stream()
                .map(Medicamento::getStockActual)
                .toList();
        System.out.println("\n5. Proyección de demanda (+25% sobre stock actual): "
                + SistemaIndoamerica.proyectarDemandaMensual(consumos, 0.25));
    }

    // -------------------------------------------------------------------------
    // UTILIDADES DE LECTURA SEGURA Y PRECARGA DE DATOS
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
                System.out.println("[!] Ingrese un número entero válido.");
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
            System.out.println("[Error en precarga inicial]: " + e.getMessage());
        }
    }
}