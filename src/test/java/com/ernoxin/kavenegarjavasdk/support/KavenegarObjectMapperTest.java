package com.ernoxin.kavenegarjavasdk.support;

import com.ernoxin.kavenegarjavasdk.model.MessageResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KavenegarObjectMapperTest {
    @Test
    void mapsLowercaseMessageId() throws Exception {
        String json = """
                {"messageid":42,"statustext":"رسیده به گیرنده","cost":120}
                """;
        MessageResult result = KavenegarObjectMapper.create().readValue(json, MessageResult.class);
        assertEquals(42L, result.messageId());
        assertEquals("رسیده به گیرنده", result.statusText());
        assertEquals(120, result.cost());
    }
}
