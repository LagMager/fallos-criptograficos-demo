import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Intentionally vulnerable, isolated A04:2025 example.
 * This class is not used by the Fuel Service.
 */
public class CryptoDemo {
    // Anyone who can read this file can decrypt every value encrypted with this key.
    private static final byte[] KEY = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);

    static String encrypt(String sensitiveData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"));
        return Base64.getEncoder().encodeToString(
                cipher.doFinal(sensitiveData.getBytes(StandardCharsets.UTF_8)));
    }

    static String decrypt(String encodedCiphertext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"));
        return new String(cipher.doFinal(Base64.getDecoder().decode(encodedCiphertext)),
                StandardCharsets.UTF_8);
    }

    public static void main(String[] args) throws Exception {
        String sample = "fuel_cost=157500;vehicle_id=demo-123";
        String ciphertext = encrypt(sample);
        System.out.println("Ciphertext: " + ciphertext);
        System.out.println("Recovered without any external secret: " + decrypt(ciphertext));
    }
}
