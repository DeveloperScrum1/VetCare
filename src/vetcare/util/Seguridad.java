package vetcare.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/** Cifrado de contraseñas con PBKDF2 (sal aleatoria + 65 536 iteraciones). Formato guardado: salHex:hashHex. */
public final class Seguridad {

    private static final int ITERACIONES = 65_536;
    private static final int BITS = 256;

    private Seguridad() { }

    public static String cifrar(String clave) {
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return aHex(sal) + ":" + aHex(derivar(clave, sal));
    }

    public static boolean verificar(String clave, String guardado) {
        if (clave == null || guardado == null || !guardado.contains(":")) return false;
        String[] p = guardado.split(":", 2);
        byte[] esperado = deHex(p[1]);
        byte[] actual = derivar(clave, deHex(p[0]));
        int dif = esperado.length ^ actual.length;
        for (int i = 0; i < Math.min(esperado.length, actual.length); i++) dif |= esperado[i] ^ actual[i];
        return dif == 0;
    }

    private static byte[] derivar(String clave, byte[] sal) {
        try {
            PBEKeySpec spec = new PBEKeySpec(clave.toCharArray(), sal, ITERACIONES, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", e);
        }
    }

    private static String aHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private static byte[] deHex(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) b[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        return b;
    }
}
