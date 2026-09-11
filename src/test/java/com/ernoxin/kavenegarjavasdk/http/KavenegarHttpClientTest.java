package com.ernoxin.kavenegarjavasdk.http;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KavenegarHttpClientTest {
    @Test
    void masksApiKeyInText() {
        String raw = "GET https://api.kavenegar.com/v1/secret-key-value/sms/send.json failed";
        String masked = KavenegarHttpClient.maskApiKeyInText(raw, "secret-key-value");
        assertEquals("GET https://api.kavenegar.com/v1/***/sms/send.json failed", masked);
        assertFalse(masked.contains("secret-key-value"));
    }

    @Test
    void masksUrlEncodedApiKeyInText() {
        String apiKey = "secret key/value";
        String encoded = java.net.URLEncoder.encode(apiKey, java.nio.charset.StandardCharsets.UTF_8);
        String raw = "I/O error on GET request for \"https://api.kavenegar.com/v1/" + encoded + "/sms/send.json\"";
        String masked = KavenegarHttpClient.maskApiKeyInText(raw, apiKey);
        assertFalse(masked.contains(apiKey));
        assertFalse(masked.contains(encoded));
        assertTrue(masked.contains("/***"));
    }

    @Test
    void queryUriPercentEncodesPlusInPhoneNumbers() {
        URI uri = KavenegarHttpClient.buildQueryUri(
                URI.create("https://api.kavenegar.com/v1/key/sms/status.json"),
                Map.of("receptor", "+989121234567")
        );
        assertTrue(uri.toString().contains("receptor=%2B989121234567"));
        assertFalse(uri.toString().contains("receptor=+989"));
    }
}
