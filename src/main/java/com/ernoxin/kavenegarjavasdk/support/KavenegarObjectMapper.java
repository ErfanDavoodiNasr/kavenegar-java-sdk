package com.ernoxin.kavenegarjavasdk.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.experimental.UtilityClass;

/**
 * Factory for the SDK Jackson {@link ObjectMapper}.
 *
 * <p>Kavenegar mixes lowercase keys ({@code messageid}) and camelCase on newer endpoints.
 * Unknown properties are ignored. Nulls are omitted on write.
 */
@UtilityClass
public class KavenegarObjectMapper {
    /**
     * Creates a mapper with SDK defaults.
     *
     * @return configured mapper
     */
    public static ObjectMapper create() {
        return JsonMapper.builder()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .build();
    }
}
