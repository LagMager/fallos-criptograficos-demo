package co.edu.univalle.fleetops.fuel.adapter.out.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** AES-GCM envelope encryption for sensitive outbox event payloads. */
@Component
final class OutboxPayloadCipher {

    private static final int NONCE_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final SecretKeySpec key;
    private final ObjectMapper objectMapper;

    OutboxPayloadCipher(@Value("${fuel.outbox.encryption-key}") String base64Key, ObjectMapper objectMapper) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        if (keyBytes.length != 32) {
            throw new IllegalArgumentException("OUTBOX_ENCRYPTION_KEY must be a Base64-encoded 32-byte key");
        }
        this.key = new SecretKeySpec(keyBytes, "AES");
        this.objectMapper = objectMapper;
    }

    String encrypt(String plaintext) throws Exception {
        byte[] nonce = new byte[NONCE_BYTES];
        RANDOM.nextBytes(nonce);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, nonce));
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        ObjectNode envelope = objectMapper.createObjectNode();
        envelope.put("alg", "AES-256-GCM");
        envelope.put("nonce", Base64.getEncoder().encodeToString(nonce));
        envelope.put("ciphertext", Base64.getEncoder().encodeToString(encrypted));
        return objectMapper.writeValueAsString(envelope);
    }

    String decrypt(JsonNode envelope) throws Exception {
        if (!"AES-256-GCM".equals(envelope.path("alg").asText())) {
            throw new IllegalArgumentException("Unsupported outbox encryption algorithm");
        }
        byte[] nonce = Base64.getDecoder().decode(envelope.path("nonce").asText());
        if (nonce.length != NONCE_BYTES) {
            throw new IllegalArgumentException("Invalid AES-GCM nonce length");
        }
        byte[] encrypted = Base64.getDecoder().decode(envelope.path("ciphertext").asText());
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, nonce));
        return new String(cipher.doFinal(encrypted), StandardCharsets.UTF_8);
    }
}
