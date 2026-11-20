package modelo;

public class ProfesionalSalud extends Persona {

    private String especialidad;
    private String colegiatura;

    public ProfesionalSalud(String dni, String nombre, int edad, String telefono, String especialidad, String colegiatura) {
        super(dni, nombre, edad, telefono);
        this.setEspecialidad(especialidad);
        this.setColegiatura(colegiatura);
    }
    public String getEspecialidad() {
        return especialidad;
    }
    public void setColegiatura(String colegiatura) {
        if (colegiatura == null || colegiatura.trim().isEmpty()) {
            throw new IllegalArgumentException("La colegiatura no puede estar vacía");
        }
        this.colegiatura = colegiatura.trim();
    }

    public String getColegiatura() {
        
        return colegiatura;
    }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad no puede estar vacía");
        }
        this.especialidad = especialidad.trim();
    }

    @Override
    public String mostrarResumen() {
        return "Profesional de Salud: " + getNombre() + ", DNI: " + obtenerDniEnmascarado() + ", Edad: " + getEdad() + ", Teléfono: " + getTelefono() + ", Especialidad: " + getEspecialidad() + ", Colegiatura: " + getColegiatura();
    }

}
