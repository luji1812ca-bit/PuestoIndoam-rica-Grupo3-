package modelo;

public abstract class Persona {
    private String dni;
    private String nombre;
    private int edad;
    private String telefono;

    public Persona(String dni, String nombre, int edad, String telefono) {
        this.setDni(dni);
        this.setNombre(nombre);
        this.setEdad(edad);
        this.setTelefono(telefono);
    }

    public String getDni() {
        return dni;
    }
    //mactches validar \\d{8} es un patrón que significa "exactamente
    //El signo ! al inicio significa "NO cumple". Es decir: "si NO tiene 8 números, lanza error".
    public void setDni(String dni) {
        if(dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener exactamente 8 números");
        }
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }
    //.trim(): Elimina todos los espacios en blanco, tabulaciones o saltos de línea que estén al inicio y al final del texto.
    //.isEmpty(): Revisa si, después de limpiar esos espacios, el texto resultante tiene longitud 0 (es decir, no quedó ningún carácter).
    // IllegalArgumentException  replazar por la exepcion pendiente de programar
    //trim se utiliza para eliminar todos los espacios en blanco solo los iniciales y finales
    public void setNombre(String nombre) {
        if(nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    public int getEdad() {
        return edad;
    }
    public void setEdad(int edad) {
        if(edad < 0 || edad > 120) {
            throw new IllegalArgumentException("La edad no puede ser negativa o mayor que 120");
        }
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        if(telefono == null || !telefono.matches("\\d{9}")) {
            throw new IllegalArgumentException("El teléfono debe tener exactamente 9 números");
        }
    
        this.telefono = telefono;
    }
    public String obtenerDniEnmascarado() {

        return "****" + dni.substring(4);
    }

    public abstract String mostrarResumen();
    
}
