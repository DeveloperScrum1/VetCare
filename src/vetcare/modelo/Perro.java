package vetcare.modelo;

public class Perro extends Animal {
    private String tamano; // "Pequeño", "Mediano", "Grande"

    public Perro(String nombre, String raza, int edad, String sexo, double peso, String tamano) {
        super(nombre, raza, edad, sexo, peso);
        this.tamano = tamano;
    }

    public String getTamano() { return tamano; }

    @Override
    public String getEspecie() { return "Perro"; }

    public String toLinea() {
        return "Perro|" + datosBase() + "|" + tamano;
    }

    @Override
    public String toString() {
        return super.toString() + " | Tamaño: " + tamano;
    }
}
