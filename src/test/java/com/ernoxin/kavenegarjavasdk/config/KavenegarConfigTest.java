package com.ernoxin.kavenegarjavasdk.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KavenegarConfigTest {
    @Test
    void toStringRedactsApiKey() {
        KavenegarConfig config = KavenegarConfig.builder("super-secret-key").build();
        String text = config.toString();
        assertFalse(text.contains("super-secret-key"));
        assertTrue(text.contains("apiKey=***"));
    }

    @Test
    void rejectsHttpBaseUrl() {
        assertThrows(RuntimeException.class, () -> KavenegarConfig.builder("key")
                .baseUrl(java.net.URI.create("http://api.kavenegar.com"))
                .build());
    }
}
