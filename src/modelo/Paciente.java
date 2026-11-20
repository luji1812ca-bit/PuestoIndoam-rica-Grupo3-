package modelo;

// extends indica que paciente  HEREDA todo de persona
public class Paciente extends Persona {
    private String numeroHistoriaClinica;
    private String tipoSeguro;

    public Paciente(String dni, String nombre, int edad, String telefono, String numeroHistoriaClinica, String tipoSeguro) {
        // super(...) llama al constructor del padre
        super(dni,nombre, edad, telefono);
        this.setNumeroHistoriaClinica(numeroHistoriaClinica);
        this.setTipoSeguro(tipoSeguro);
        
    }
    public String getNumeroHistoriaClinica() {
        return numeroHistoriaClinica;
    }
    public void setNumeroHistoriaClinica(String numeroHistoriaClinica) {
       if(numeroHistoriaClinica == null || numeroHistoriaClinica.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de historia clínica no puede estar vacío");   
        }
        this.numeroHistoriaClinica = numeroHistoriaClinica.trim();   
    }
    public String getTipoSeguro() {
        return tipoSeguro;
    }   
    public void setTipoSeguro(String tipoSeguro) {
        if(tipoSeguro == null || tipoSeguro.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de seguro no puede estar vacío");
        }
        this.tipoSeguro = tipoSeguro.trim();
    }
    @Override
    public String mostrarResumen(){
        return "Paciente: " + getNombre() + ", DNI: " + obtenerDniEnmascarado() + ", Edad: " + getEdad() + ", Teléfono: " + getTelefono() + ", Historia Clínica: " + getNumeroHistoriaClinica() + ", Tipo de Seguro: " + getTipoSeguro();
    }
}
