package modelo;

// Patrón Factory: centraliza la creación de Paciente y ProfesionalSalud según el tipo solicitado.
public class PersonaFactory {

    // "static" permite llamar a PersonaFactory.crearPersona(...) sin instanciar la fábrica.
    // Devuelve el tipo base (Persona), pero por dentro construye un Paciente o un ProfesionalSalud.
    public static Persona crearPersona(String tipo, String dni, String nombre, int edad, String telefono) {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de persona no puede estar vacío.");
        }

        // toLowerCase() convierte el texto a minúsculas para que el switch no falle por mayúsculas.
        switch (tipo.trim().toLowerCase()) {
            case "profesional":
                return new ProfesionalSalud(dni, nombre, edad, telefono, "Medicina General", "Por asignar");
            case "paciente":
                return new Paciente(dni, nombre, edad, telefono, "HC-PENDIENTE", "SIS");
            default:
                throw new IllegalArgumentException("Tipo de persona desconocido: " + tipo);
        }
    }
}
 