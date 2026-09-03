package com.ernoxin.kavenegarjavasdk.http;

import com.ernoxin.kavenegarjavasdk.exception.KavenegarApiException;
import com.ernoxin.kavenegarjavasdk.model.PageMetadata;
import com.ernoxin.kavenegarjavasdk.model.PagedResult;
import com.ernoxin.kavenegarjavasdk.support.KavenegarErrorCatalog;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Parses Kavenegar envelopes {@code { return: { status, message }, entries, metadata? }}.
 *
 * <p>Success requires HTTP 2xx and {@code return.status == 200}.
 */
public final class KavenegarResponseParser {
    static final int SUCCESS_STATUS = 200;

    private final ObjectMapper mapper;

    /**
     * Creates a parser.
     *
     * @param mapper Jackson mapper
     */
    public KavenegarResponseParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Parses and maps the {@code entries} node.
     *
     * @param response raw HTTP response
     * @param dataType target Java type of {@code entries}
     * @param <T>      result type
     * @return mapped entries
     */
    public <T> T parse(ResponseEntity<String> response, JavaType dataType) {
        JsonNode root = requireSuccess(response);
        return convertEntries(root.get("entries"), dataType, response);
    }

    /**
     * Parses {@code entries} as a list plus optional {@code metadata}.
     *
     * @param response   raw HTTP response
     * @param entryClass row class
     * @param <T>        row type
     * @return paged result
     */
    public <T> PagedResult<T> parsePaged(ResponseEntity<String> response, Class<T> entryClass) {
        JsonNode root = requireSuccess(response);
        JavaType listType = mapper.getTypeFactory().constructCollectionType(List.class, entryClass);
        List<T> entries = convertEntries(root.get("entries"), listType, response);
        PageMetadata metadata = null;
        JsonNode metadataNode = root.get("metadata");
        if (metadataNode != null && !metadataNode.isNull() && !metadataNode.isMissingNode()) {
            metadata = mapper.convertValue(metadataNode, PageMetadata.class);
        }
        return new PagedResult<>(entries, metadata);
    }

    /**
     * Parses a successful envelope whose {@code entries} may be absent (delete template).
     *
     * @param response raw HTTP response
     */
    public void parseEmpty(ResponseEntity<String> response) {
        requireSuccess(response);
    }

    private JsonNode requireSuccess(ResponseEntity<String> response) {
        int httpStatus = response.getStatusCode().value();
        String body = response.getBody();
        if (body == null || body.isBlank()) {
            throw new KavenegarApiException(httpStatus, null, "Empty response body", body);
        }
        JsonNode root = readTree(httpStatus, body);
        JsonNode returnNode = root.get("return");
        Integer status = extractCode(returnNode == null ? null : returnNode.get("status"));
        String message = textOrNull(returnNode == null ? null : returnNode.get("message"));
        if (status == null || status != SUCCESS_STATUS) {
            throw new KavenegarApiException(httpStatus, status, resolveMessage(status, message), body);
        }
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new KavenegarApiException(httpStatus, status, resolveMessage(status, message), body);
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertEntries(JsonNode entries, JavaType dataType, ResponseEntity<String> response) {
        int httpStatus = response.getStatusCode().value();
        String body = response.getBody();
        Class<?> raw = dataType.getRawClass();
        if (entries == null || entries.isMissingNode() || entries.isNull()) {
            if (List.class.isAssignableFrom(raw)) {
                return (T) List.of();
            }
            throw new KavenegarApiException(httpStatus, SUCCESS_STATUS, "Missing entries in response", body);
        }
        JsonNode node = entries;
        if (List.class.isAssignableFrom(raw) && node.isObject()) {
            var array = mapper.createArrayNode();
            array.add(node);
            node = array;
        }
        try {
            return mapper.convertValue(node, dataType);
        } catch (IllegalArgumentException ex) {
            throw new KavenegarApiException(httpStatus, SUCCESS_STATUS, "Invalid response body", body, ex);
        }
    }

    private JsonNode readTree(int status, String body) {
        try {
            return mapper.readTree(body);
        } catch (JacksonException ex) {
            throw new KavenegarApiException(status, null, "Invalid JSON response", body, ex);
        }
    }

    private Integer extractCode(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isInt() || node.isLong() || node.isNumber()) {
            return node.asInt();
        }
        if (node.isTextual()) {
            try {
                return Integer.parseInt(node.asText().trim());
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private String textOrNull(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return null;
        }
        if (node.isTextual()) {
            String value = node.asText();
            return value != null && !value.isBlank() ? value : null;
        }
        return null;
    }

    private String resolveMessage(Integer status, String fallback) {
        String catalog = KavenegarErrorCatalog.messageFor(status);
        if (catalog != null && !catalog.isBlank()) {
            return catalog;
        }
        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "Kavenegar API error";
    }
}
