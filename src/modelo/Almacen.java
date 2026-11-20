package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Se eligio el patron Singleton para garantizar una unica instancia centralizada del inventario en todo el puesto de salud.
public class Almacen {

    private static Almacen instancia;
    private List<Medicamento> medicamentos;

    // Constructor privado: evita que se creen multiples almacenes con 'new Almacen()'
    private Almacen() {
        this.medicamentos = new ArrayList<>();
    }

    // Punto de acceso global a la unica instancia
    public static Almacen getInstancia() {
        if (instancia == null) {
            instancia = new Almacen();
        }
        return instancia;
    }

    // Agregacion: recibe medicamentos ya creados y los agrega al inventario
    public void agregarMedicamento(Medicamento medicamento) {
        if (medicamento == null) {
            throw new IllegalArgumentException("Error: los datos del medicamento son obligatorios para el registro");
        }
        this.medicamentos.add(medicamento);
    }

    public Medicamento buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        for (Medicamento m : medicamentos) {
            if (m.getCodigo().equalsIgnoreCase(codigo.trim())) {
                return m;
            }
        }
        return null;
    }

    // Devuelve una vista inmutable de la lista para proteger el encapsulamiento
    public List<Medicamento> getMedicamentos() {
        return Collections.unmodifiableList(medicamentos);
    }
}