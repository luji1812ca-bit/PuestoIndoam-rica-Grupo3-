package servicio;
import excepciones.DniInvalidoException;
import excepciones.PacienteDuplicadoException;
import excepciones.StockInsuficienteException;
import modelo.Almacen;
import modelo.CitaMedica;
import modelo.Medicamento;
import modelo.Paciente;
import modelo.ProfesionalSalud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SistemaIndoamerica {

    private final List<Paciente> pacientes;
    private final List<ProfesionalSalud> profesionales;
    private final List<CitaMedica> citas;
    private final Almacen almacen;

    public SistemaIndoamerica() {
        this.pacientes = new ArrayList<>();
        this.profesionales = new ArrayList<>();
        this.citas = new ArrayList<>();
        this.almacen = Almacen.getInstancia(); // Singleton del Tema 3
    }
    
    // BLOQUE A: VALIDACIÓN CON REGEX (TEMA 1) Y EXCEPCIONES PROPIAS (TEMA 4.4)
    
    public void registrarPaciente(Paciente nuevoPaciente)
            throws DniInvalidoException, PacienteDuplicadoException {

        // Gramática/Regex del Tema 1: exactamente 8 dígitos numéricos
        if (nuevoPaciente == null || !nuevoPaciente.getDni().matches("\\d{8}")) {
            throw new DniInvalidoException("DNI inválido: Debe tener exactamente 8 dígitos numéricos.");
        }

        // Validación declarativa con Stream para evitar duplicados
        boolean existe = pacientes.stream()
                .anyMatch(p -> p.getDni().equals(nuevoPaciente.getDni()));

        if (existe) {
            throw new PacienteDuplicadoException(
                    "El paciente con DNI " + nuevoPaciente.obtenerDniEnmascarado() + " ya está registrado.");
        }

        pacientes.add(nuevoPaciente);
    }

    public void registrarProfesional(ProfesionalSalud profesional) throws DniInvalidoException {
        if (profesional == null || !profesional.getDni().matches("\\d{8}")) {
            throw new DniInvalidoException("DNI inválido para el profesional de salud.");
        }
        profesionales.add(profesional);
    }

    public void registrarCita(CitaMedica cita) {
        if (cita != null) {
            citas.add(cita);
        }
    }

    public void dispensarMedicamentoEnCita(CitaMedica cita, Medicamento medicamento, int cantidad)
            throws StockInsuficienteException {
        if (cita == null || medicamento == null) {
            throw new IllegalArgumentException("La cita y el medicamento son obligatorios.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a dispensar debe ser mayor que 0: " + cantidad);
        }
        if (medicamento.getStockActual() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente de " + medicamento.getNombre()
                    + ". Disponible: " + medicamento.getStockActual()
                    + ", solicitado: " + cantidad);
        }

        medicamento.descontarStock(cantidad);
        cita.agregarMedicamentoRecetado(medicamento, cantidad); // Composición del Tema 3
    }

        // BLOQUE B: FUNCIONES PURAS E INMUTABILIDAD (TEMA 4.1)
    
    /*- Función PURA (estilo Inmutables.java del Tema 4):
     * - No modifica la lista original 'consumosActuales' (evita alias).
     * - Crea una lista nueva y devuelve una copia inmutable con List.copyOf().
     * - No imprime en consola ni depende de variables externas mutables.*/

    public static List<Integer> proyectarDemandaMensual(List<Integer> consumosActuales, double porcentajeAumento) {
        List<Integer> proyeccion = new ArrayList<>();
        for (int consumo : consumosActuales) {
            int estimado = (int) Math.ceil(consumo * (1 + porcentajeAumento));
            proyeccion.add(estimado);
        }
        return List.copyOf(proyeccion);
    }

    // Función PURA determinista para clasificar el nivel de alerta de un medicamento.
     
    public static String evaluarNivelAlertaStock(int stockActual, int stockMinimo) {
        if (stockActual <= 0) {
            return "AGOTADO";
        }
        if (stockActual <= stockMinimo) {
            return "CRÍTICO";
        }
        if (stockActual <= stockMinimo * 1.5) {
            return "PREVENTIVO";
        }
        return "ÓPTIMO";
    }

        // BLOQUE C: FUNCIONES DE ORDEN SUPERIOR Y LAMBDAS (TEMA 4.2)
    /* Orden superior 1: recibe un Predicate<Paciente> (criterio dinámico).
     * Permite filtrar por seguro, edad, etc., sin escribir múltiples bucles.*/
     
    public List<Paciente> filtrarPacientesPorCriterio(Predicate<Paciente> criterio) {
        if (criterio == null) {
            throw new IllegalArgumentException("El criterio de filtrado no puede ser nulo.");
        }
        List<Paciente> resultado = new ArrayList<>();
        for (Paciente p : pacientes) {
            if (criterio.test(p)) {
                resultado.add(p);
            }
        }
        return List.copyOf(resultado);
    }

    /* Orden superior 2: recibe una Function<Medicamento, String> para formatear
     * reportes distintos con un mismo recorrido.*/
    
    public List<String> generarReporteMedicamentos(Function<Medicamento, String> formato) {
        List<String> reporte = new ArrayList<>();
        for (Medicamento m : almacen.getMedicamentos()) {
            reporte.add(formato.apply(m));
        }
        return List.copyOf(reporte);
    }

    // BLOQUE D: STREAMS DECLARATIVOS: filter, map, reduce, collect (TEMA 4.3)
    // 1. FILTER + MAP + toList(): nombres de medicamentos en stock crítico

    public List<String> obtenerNombresStockCritico() {
        return almacen.getMedicamentos().stream()
                .filter(m -> m.getStockActual() <= m.getStockMinimo())
                .map(m -> m.getNombre())
                .toList();
    }

    // 2. FILTER + MAP + toList(): resúmenes enmascarados de pacientes por tipo de seguro (ej. "SIS")

    public List<String> listarResumenesPorSeguro(String tipoSeguro) {
        final String seguroBuscado = tipoSeguro.trim(); // effectively final para la lambda
        return pacientes.stream()
                .filter(p -> p.getTipoSeguro().equalsIgnoreCase(seguroBuscado))
                .map(p -> p.mostrarResumen())
                .toList();
    }

    // 3. MAP + REDUCE: total global de unidades dispensadas en todas las citas

    public int calcularTotalUnidadesDispensadas() {
        return citas.stream()
                .flatMap(c -> c.getDetalleReceta().stream())
                .map(d -> d.getCantidadEntregada())
                .reduce(0, (acc, cantidad) -> acc + cantidad);
    }

    // 4. COLLECT a SET: especialidades médicas únicas registradas sin repetidos

    public Set<String> obtenerEspecialidadesUnicas() {
        return profesionales.stream()
                .map(prof -> prof.getEspecialidad())
                .collect(Collectors.toSet());
    }

    // 5. COLLECT a MAP: diccionario código -> stockActual para consulta directa O(1)

    public Map<String, Integer> obtenerMapaStockPorCodigo() {
        return almacen.getMedicamentos().stream()
                .collect(Collectors.toMap(
                        m -> m.getCodigo(),
                        m -> m.getStockActual()
                ));
    }

    // GETTERS DEFENSIVOS (Copias inmutables para proteger las listas internas)

    public List<Paciente> getPacientes() {
        return List.copyOf(pacientes);
    }

    public List<ProfesionalSalud> getProfesionales() {
        return List.copyOf(profesionales);
    }

    public List<CitaMedica> getCitas() {
        return List.copyOf(citas);
    }
}