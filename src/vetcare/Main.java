package vetcare;

import vetcare.gestion.SistemaVetCare;

public class Main {
    public static void main(String[] args) {
        String carpetaData = "data";
        SistemaVetCare sistema = new SistemaVetCare(carpetaData);
        sistema.iniciar();
    }
}
