# Sistema de Gestión y Asignación de Turnos — Puesto de Salud Indoamérica

Proyecto académico del curso **Lenguajes de Programación** (Ingeniería de Sistemas Computacionales — Universidad Privada del Norte, Trujillo - Perú). Modela y automatiza el flujo operativo de admisión, citas médicas y control de farmacia del **Puesto de Salud Indoamérica (Categoría I-2, Microred El Porvenir)** aplicando una arquitectura multiparadigma en Java (POO + Programación Funcional) y cumplimiento de la **Ley N.° 29733** (enmascaramiento de DNI).

---

## 1. Problemática que resuelve

* **Desorden y duplicidad en el registro de pacientes:** Evita registrar dos veces al mismo paciente mediante reglas de unicidad (`PacienteDuplicadoException`), valida el formato del DNI de 8 dígitos (`DniInvalidoException`) y enmascara su visualización en consola (`****5678`).
* **Falta de control en el inventario de medicamentos:** Centraliza el seguimiento de existencias mediante un almacén único (`Almacen` con patrón Singleton) e impide despachar más unidades de las disponibles (`StockInsuficienteException`).
* **Desorganización en citas médicas y atención clínica:** Estructura la relación entre pacientes, profesionales de salud y citas médicas (`CitaMedica`), vinculando cada atención con su prescripción (`DetalleReceta`).
* **Escalabilidad y mantenimiento:** Separa estrictamente dominio (`modelo`), lógica de negocio (`servicio`), errores controlados (`excepciones`) e interfaz de consola (`Main.java`), centralizando la creación de personas con `PersonaFactory`.

---

## 2. Integrantes y Roles

| Código | Integrante | Rol en el Equipo |
| :--- | :--- | :--- |
| **N00552916** | Vigo Pizán, Fernando Javier | Diseñador de Clases y Arquitectura |
| **N00554775** | Tantaleán Rodríguez, Milton Jhonatan | Programador de Lógica y Funcional |
| **N00535983** | Cruz Ayala, Lucero Jimena | Integrador (GitHub y Documentación) |
| **N00537543** | Gonzalez Rodríguez, Rosita Esther | Integrador (Consola y Pruebas) |

---

## 3. Estructura del Repositorio

    PuestoIndoam-rica-Grupo3-/
    ├── docs/
    │   └── diagrama-clases.png          # Diagrama de clases UML del proyecto
    ├── src/
    │   ├── excepciones/
    │   │   ├── DniInvalidoException.java
    │   │   ├── PacienteDuplicadoException.java
    │   │   └── StockInsuficienteException.java
    │   ├── modelo/
    │   │   ├── Almacen.java
    │   │   ├── CitaMedica.java
    │   │   ├── DetalleReceta.java
    │   │   ├── Medicamento.java
    │   │   ├── Paciente.java
    │   │   ├── Persona.java
    │   │   ├── PersonaFactory.java
    │   │   └── ProfesionalSalud.java
    │   ├── servicio/
    │   │   └── SistemaIndoamerica.java
    │   └── Main.java
    └── README.md

---

## 4. Lenguaje, Versión y Cómo Ejecutar

* **Lenguaje:** Java (versión mínima **JDK 16** por el uso de `switch` con flechas `->` y `.toList()` en `Streams`; recomendado **OpenJDK 17 o 21**).

### Ejecución desde terminal (Windows / Linux / macOS)

1. Verifica que Java esté instalado ejecutando `java -version` y `javac -version`.
2. Abre una terminal en la **carpeta raíz del proyecto** (donde se encuentra `src/`).
3. Compila todos los paquetes hacia la carpeta `out`:
   `javac -d out src/excepciones/*.java src/modelo/*.java src/servicio/*.java src/Main.java`
4. Ejecuta el sistema por consola:
   `java -cp out Main`
5. Escribe un número del **1 al 6** para navegar por las opciones o **0** para salir. *(El sistema precarga 3 medicamentos, 2 pacientes y 2 profesionales para pruebas rápidas)*.

> **Nota para VS Code:** También puedes abrir `src/Main.java` directamente y presionar **Run** sobre el método `main`.

---

## 5. Tabla de Evidencias (Tema → Archivo)

| Tema aplicado | Archivo | Evidencia técnica |
| :--- | :--- | :--- |
| **Encapsulamiento y Ley N.° 29733** | `src/modelo/Persona.java` | Atributos `private`, validaciones Regex en *setters* y enmascaramiento de DNI (`obtenerDniEnmascarado()` → `****5678`). |
| **Herencia y Polimorfismo** | `src/modelo/Paciente.java`, `ProfesionalSalud.java`, `CitaMedica.java` | Subclases que extienden `Persona` sobrescribiendo `mostrarResumen()` y sobrecarga de `agregarMedicamentoRecetado()`. |
| **Asociación, Agregación y Composición** | `src/modelo/CitaMedica.java`, `Almacen.java`, `DetalleReceta.java` | Asociación con `Paciente`/`ProfesionalSalud`, agregación de `Medicamento` en `Almacen` y composición de `DetalleReceta` en `CitaMedica`. |
| **Patrón Singleton** | `src/modelo/Almacen.java` | `Almacen.getInstancia()` garantiza una única instancia centralizada del inventario en memoria. |
| **Patrón Factory** | `src/modelo/PersonaFactory.java` | `PersonaFactory.crearPersona(...)` centraliza y desacopla la creación de `Paciente` y `ProfesionalSalud`. |
| **Excepciones personalizadas** | `src/excepciones/*.java` y `src/Main.java` | `DniInvalidoException`, `PacienteDuplicadoException` y `StockInsuficienteException` capturadas con bloques `try-catch` sin caída del sistema. |
| **Streams, Lambdas y Predicate** | `src/servicio/SistemaIndoamerica.java` | Reportes operativos con `filter`, `map`, `flatMap`, `reduce`, `collect` y filtros dinámicos con `Predicate<Paciente>`. |
| **Funciones puras e inmutabilidad** | `src/servicio/SistemaIndoamerica.java` | `proyectarDemandaMensual(...)`, `evaluarNivelAlertaStock(...)` y colecciones protegidas con `List.copyOf` / `Collections.unmodifiableList`. |
| **Validación de entrada en consola** | `src/Main.java` | Bucle `while`/`switch` con `leerEntero()` que captura `NumberFormatException` ante entradas no numéricas. |
