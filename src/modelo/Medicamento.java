package modelo;

public class Medicamento {

    private String codigo;
    private String nombre;
    private int stockActual;
    private int stockMinimo;

    public Medicamento(String codigo, String nombre, int stockActual, int stockMinimo) {
        this.setCodigo(codigo);
        this.setNombre(nombre);
        this.setStockActual(stockActual);
        this.setStockMinimo(stockMinimo);
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        this.codigo = codigo.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        if (stockActual < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo.");
        }
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        if (stockMinimo < 0) {
            throw new IllegalArgumentException("El stock mínimo no puede ser negativo.");
        }
        this.stockMinimo = stockMinimo;
    }

    // Devuelve true si el stock llegó al límite mínimo o bajó de él
    public boolean necesitaReposicion() {
        return stockActual <= stockMinimo;
    }

    // Descuenta stock validando que la cantidad sea válida y haya disponibilidad (Encapsulamiento)
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor a cero.");
        }
        if (cantidad > stockActual) {
            throw new IllegalArgumentException("No hay suficiente stock disponible.");
        }
        this.stockActual = this.stockActual - cantidad;
    }

    public String mostrarDetalle() {
        return "[" + codigo + "] " + nombre + " | Stock actual: " + stockActual + " | Stock mínimo: " + stockMinimo;
    }
}