package com.financeledger.financeledger.security;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@Slf4j
public class HmacUtil {

    private static final String ALGORITHM = "HmacSHA256";

    public String computeHmac(String payload, String secret) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(
                    secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            mac.init(keySpec);
            byte[] hash = mac.doFinal(
                    payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            log.error("Error computing HMAC: {}", e.getMessage());
            throw new RuntimeException("HMAC computation failed");
        }
    }

    public boolean verifyHmac(String payload, String secret,
                              String expectedSignature) {
        String computed = computeHmac(payload, secret);
        return computed.equals(expectedSignature);
    }

}
