package modelo;

public class DetalleReceta {

    private int cantidadEntregada;
    private String indicacion;
    private Medicamento medicamento;

    public DetalleReceta(Medicamento medicamento, int cantidadEntregada, String indicacion) {
        this.setMedicamento(medicamento);
        this.setCantidadEntregada(cantidadEntregada);
        this.setIndicacion(indicacion);
    }

    public int getCantidadEntregada() {
        return cantidadEntregada;
    }

    public void setCantidadEntregada(int cantidadEntregada) {
        if (cantidadEntregada <= 0) {
            throw new IllegalArgumentException("La cantidad entregada debe ser mayor a cero.");
        }
        this.cantidadEntregada = cantidadEntregada;
    }

    public String getIndicacion() {
        return indicacion;
    }

    public void setIndicacion(String indicacion) {
        if (indicacion == null || indicacion.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar la indicación médica.");
        }
        this.indicacion = indicacion.trim();
    }

    public Medicamento getMedicamento() {
        return medicamento;
    }

    public void setMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            throw new IllegalArgumentException("Debe asociar un medicamento válido al detalle de receta.");
        }
        this.medicamento = medicamento;
    }
}