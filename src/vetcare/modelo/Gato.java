package vetcare.modelo;

public class Gato extends Animal {
    private boolean esCastrado;

    public Gato(String nombre, String raza, int edad, String sexo, double peso, boolean esCastrado) {
        super(nombre, raza, edad, sexo, peso);
        this.esCastrado = esCastrado;
    }

    public boolean isEsCastrado() { return esCastrado; }

    @Override
    public String getEspecie() { return "Gato"; }

    public String toLinea() {
        return "Gato|" + datosBase() + "|" + esCastrado;
    }

    @Override
    public String toString() {
        return super.toString() + " | Castrado: " + (esCastrado ? "Sí" : "No");
    }
}
