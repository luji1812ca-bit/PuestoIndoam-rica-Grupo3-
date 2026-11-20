package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CitaMedica {

    private String codigoCita;
    private String fecha;
    private String hora;
    private String motivo;
    private Paciente paciente;
    private ProfesionalSalud profesionalSalud;
    private List<git pull origin main> detalleReceta;

    public CitaMedica(String codigoCita, String fecha, String hora, String motivo,
                      Paciente paciente, ProfesionalSalud profesionalSalud) {
        this.setCodigoCita(codigoCita);
        this.setFecha(fecha);
        this.setHora(hora);
        this.setMotivo(motivo);
        this.setPaciente(paciente);
        this.setProfesionalSalud(profesionalSalud);
        this.detalleReceta = new ArrayList<>();
    }

    public String getCodigoCita() {
        return codigoCita;
    }

    public void setCodigoCita(String codigoCita) {
        if (codigoCita == null || codigoCita.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de la cita es obligatorio.");
        }
        this.codigoCita = codigoCita.trim();
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha de la cita es obligatoria.");
        }
        this.fecha = fecha.trim();
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        if (hora == null || hora.trim().isEmpty()) {
            throw new IllegalArgumentException("La hora de la cita es obligatoria.");
        }
        this.hora = hora.trim();
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar el motivo de la consulta.");
        }
        this.motivo = motivo.trim();
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("Debe asignar un paciente válido a la cita.");
        }
        this.paciente = paciente;
    }

    public ProfesionalSalud getProfesionalSalud() {
        return profesionalSalud;
    }

    public void setProfesionalSalud(ProfesionalSalud profesionalSalud) {
        if (profesionalSalud == null) {
            throw new IllegalArgumentException("Debe asignar un profesional de salud válido a la cita.");
        }
        this.profesionalSalud = profesionalSalud;
    }

    public List<DetalleReceta> getDetalleReceta() {
        return Collections.unmodifiableList(detalleReceta);
    }

    // Composición: CitaMedica crea internamente el DetalleReceta y descuenta el stock
    public void agregarMedicamentoRecetado(Medicamento medicamento, int cantidad) {
        agregarMedicamentoRecetado(medicamento, cantidad, "Según indicación médica");
    }

    // Sobrecarga de método: permite agregar también una indicación específica
    public void agregarMedicamentoRecetado(Medicamento medicamento, int cantidad, String indicacion) {
        if (medicamento == null) {
            throw new IllegalArgumentException("Debe seleccionar un medicamento válido.");
        }
        medicamento.descontarStock(cantidad);
        DetalleReceta detalle = new DetalleReceta(medicamento, cantidad, indicacion);
        this.detalleReceta.add(detalle);
    }

    public String mostrarResumenCita() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("Cita [").append(codigoCita).append("] - ")
               .append(fecha).append(" ").append(hora).append("\n")
               .append("Paciente: ").append(paciente.getNombre())
               .append(" (DNI: ").append(paciente.obtenerDniEnmascarado()).append(")\n")
               .append("Profesional: ").append(profesionalSalud.getNombre())
               .append(" - ").append(profesionalSalud.getEspecialidad()).append("\n")
               .append("Motivo: ").append(motivo).append("\n")
               .append("Medicamentos recetados: ").append(detalleReceta.size());
        return resumen.toString();
    }
}