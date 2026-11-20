package excepciones;

public class PacienteDuplicadoException extends Exception {
    public PacienteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
