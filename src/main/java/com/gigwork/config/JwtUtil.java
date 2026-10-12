package com.gigwork.config;

import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

@Component
public class JwtUtil {

    private static final String SECRET = "gigwork_secret_key_2026_secure_hmac_sha256_token";
    private static final long EXPIRATION_MILLIS = 1000L * 60 * 60 * 24 * 7; // 7 days

    public String generateToken(String email) {
        long now = System.currentTimeMillis();
        long exp = now + EXPIRATION_MILLIS;

        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payloadJson = String.format("{\"sub\":\"%s\",\"iat\":%d,\"exp\":%d}", email, now / 1000, exp / 1000);
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));

        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public String extractEmail(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return null;
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int subIndex = payloadJson.indexOf("\"sub\":\"");
            if (subIndex == -1) return null;
            int start = subIndex + 7;
            int end = payloadJson.indexOf("\"", start);
            if (end == -1) return null;
            return payloadJson.substring(start, end);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) return false;

            String expectedSig = sign(parts[0] + "." + parts[1]);
            if (!MessageDigest.isEqual(expectedSig.getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
                return false;
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            int expIndex = payloadJson.indexOf("\"exp\":");
            if (expIndex != -1) {
                int start = expIndex + 6;
                int end = start;
                while (end < payloadJson.length() && Character.isDigit(payloadJson.charAt(end))) {
                    end++;
                }
                long exp = Long.parseLong(payloadJson.substring(start, end).trim());
                if (System.currentTimeMillis() / 1000 > exp) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String sign(String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Could not compute signature", e);
        }
    }
}
