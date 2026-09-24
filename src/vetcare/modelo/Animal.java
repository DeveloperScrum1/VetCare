package vetcare.modelo;

public abstract class Animal {
    private String nombre;
    private String raza;
    private int    edad;    // en años
    private String sexo;    // "Macho" o "Hembra"
    private double peso;    // en kilogramos

    public Animal(String nombre, String raza, int edad, String sexo, double peso) {
        this.nombre = nombre;
        this.raza   = raza;
        this.edad   = edad;
        this.sexo   = sexo;
        this.peso   = peso;
    }

    public String getNombre() { return nombre; }
    public String getRaza()   { return raza; }
    public int    getEdad()   { return edad; }
    public String getSexo()   { return sexo; }
    public double getPeso()   { return peso; }

    public void setEdad(int edad)     { this.edad = edad; }
    public void setPeso(double peso)  { this.peso = peso; }

    // Método abstracto — cada subclase retorna su especie
    public abstract String getEspecie();

    // Datos para persistencia: subclases agregan sus atributos extra
    public String datosBase() {
        return nombre + "|" + raza + "|" + edad + "|" + sexo + "|" + peso;
    }

    @Override
    public String toString() {
        return getEspecie() + " — " + nombre + " (" + raza + ", " + edad
                + " año(s), " + sexo + ", " + peso + " kg)";
    }
}
