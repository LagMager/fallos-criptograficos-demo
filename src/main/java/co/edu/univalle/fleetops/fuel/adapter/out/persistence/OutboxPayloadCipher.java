package co.edu.univalle.fleetops.fuel.adapter.out.persistence;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/** Intentionally vulnerable A04:2025 demonstration cipher. Do not use with real data. */
final class OutboxPayloadCipher {

    private static final byte[] DEMO_KEY = "FuelDemoKey12345".getBytes(StandardCharsets.UTF_8);

    private OutboxPayloadCipher() {
    }

    static String encrypt(String plaintext) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(DEMO_KEY, "AES"));
        return Base64.getEncoder().encodeToString(cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8)));
    }

    static String decrypt(String encoded) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(DEMO_KEY, "AES"));
        return new String(cipher.doFinal(Base64.getDecoder().decode(encoded)), StandardCharsets.UTF_8);
    }
}
