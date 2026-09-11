package com.ernoxin.kavenegarjavasdk.http;

import com.ernoxin.kavenegarjavasdk.config.KavenegarConfig;
import com.ernoxin.kavenegarjavasdk.exception.KavenegarTransportException;
import com.ernoxin.kavenegarjavasdk.exception.KavenegarValidationException;
import com.ernoxin.kavenegarjavasdk.model.PagedResult;
import com.ernoxin.kavenegarjavasdk.support.KavenegarObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Low-level HTTP client for Kavenegar REST.
 *
 * <p>The API key is a path segment ({@code /v1/{API-KEY}/method.json}). Send, verify, cancel,
 * config updates, template mutations, and unread inbox fetches never retry.
 */
public final class KavenegarHttpClient {
    private static final ResponseExtractor<ResponseEntity<String>> RESPONSE_EXTRACTOR = response -> {
        String responseBody = null;
        try (InputStream stream = response.getBody()) {
            if (stream != null) {
                responseBody = StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
            }
        }
        return new ResponseEntity<>(responseBody, response.getHeaders(), response.getStatusCode());
    };

    private final KavenegarConfig config;
    private final RestTemplate restTemplate;
    private final KavenegarResponseParser responseParser;

    /**
     * Creates a client with explicit dependencies.
     *
     * @param config       config
     * @param restTemplate transport
     * @param mapper       JSON mapper
     */
    public KavenegarHttpClient(KavenegarConfig config, RestTemplate restTemplate, ObjectMapper mapper) {
        this.config = config;
        this.restTemplate = restTemplate;
        this.responseParser = new KavenegarResponseParser(mapper);
        configureRestTemplate(restTemplate, config);
    }

    /**
     * Creates the default JDK-backed client.
     *
     * @param config config
     * @return HTTP client
     */
    public static KavenegarHttpClient create(KavenegarConfig config) {
        ObjectMapper mapper = KavenegarObjectMapper.create();
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(config.connectTimeout())
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new KavenegarHttpClient(config, restTemplate, mapper);
    }

    private static void configureRestTemplate(RestTemplate restTemplate, KavenegarConfig config) {
        ClientHttpRequestFactory requestFactory = restTemplate.getRequestFactory();
        if (requestFactory instanceof SimpleClientHttpRequestFactory simpleFactory) {
            simpleFactory.setConnectTimeout((int) config.connectTimeout().toMillis());
            simpleFactory.setReadTimeout((int) config.readTimeout().toMillis());
        } else if (requestFactory instanceof JdkClientHttpRequestFactory jdkFactory) {
            jdkFactory.setReadTimeout(config.readTimeout());
        }
        if (restTemplate.getErrorHandler() instanceof DefaultResponseErrorHandler) {
            restTemplate.setErrorHandler(new ResponseErrorHandler() {
                @Override
                public boolean hasError(ClientHttpResponse response) {
                    return false;
                }

                @Override
                public void handleError(URI url, HttpMethod method, ClientHttpResponse response) {
                }
            });
        }
    }

    static String maskApiKeyInText(String text, String apiKey) {
        if (text == null || apiKey == null || apiKey.isBlank()) {
            return text;
        }
        String masked = text.replace(apiKey, "***");
        String encodedKey = URLEncoder.encode(apiKey, StandardCharsets.UTF_8);
        if (!encodedKey.equals(apiKey)) {
            masked = masked.replace(encodedKey, "***");
        }
        return masked;
    }

    /**
     * Builds a query URI with values percent-encoded up front (so {@code +} becomes {@code %2B}).
     * Spring's default query encoder leaves {@code +} literal, which servers treat as space.
     */
    static URI buildQueryUri(URI base, Map<String, ?> query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(base);
        if (query != null) {
            for (Map.Entry<String, ?> entry : query.entrySet()) {
                if (entry.getValue() != null) {
                    builder.queryParam(entry.getKey(), urlEncode(String.valueOf(entry.getValue())));
                }
            }
        }
        return builder.build(true).toUri();
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    /**
     * POST {@code application/x-www-form-urlencoded}. Never retried.
     *
     * @param method   REST method path
     * @param form     form fields; null values skipped
     * @param dataType entries type
     * @param <T>      result type
     * @return parsed entries
     */
    public <T> T postForm(String method, Map<String, ?> form, JavaType dataType) {
        return execute(HttpMethod.POST, methodUri(method), encodeForm(form), formHeaders(), dataType, false);
    }

    /**
     * GET with optional query. Retry only when {@code retryable} is true.
     *
     * @param method    REST method path
     * @param query     query map
     * @param dataType  entries type
     * @param retryable whether transport retries are allowed
     * @param <T>       result type
     * @return parsed entries
     */
    public <T> T get(String method, Map<String, ?> query, JavaType dataType, boolean retryable) {
        return execute(HttpMethod.GET, queryUri(methodUri(method), query), null, jsonHeaders(), dataType, retryable);
    }

    /**
     * GET using API key {@code 0} (public utils).
     *
     * @param method    REST method path
     * @param dataType  entries type
     * @param retryable whether transport retries are allowed
     * @param <T>       result type
     * @return parsed entries
     */
    public <T> T getPublic(String method, JavaType dataType, boolean retryable) {
        return execute(HttpMethod.GET, methodUri("0", method), null, jsonHeaders(), dataType, retryable);
    }

    /**
     * GET paged {@code entries} + {@code metadata}.
     *
     * @param method     REST method path
     * @param query      query map
     * @param entryClass row class
     * @param retryable  whether transport retries are allowed
     * @param <T>        row type
     * @return paged result
     */
    public <T> PagedResult<T> getPaged(String method, Map<String, ?> query, Class<T> entryClass, boolean retryable) {
        ResponseEntity<String> response = exchange(
                HttpMethod.GET,
                queryUri(methodUri(method), query),
                null,
                jsonHeaders(),
                retryable
        );
        return responseParser.parsePaged(response, entryClass);
    }

    /**
     * DELETE with optional query. Never retried.
     *
     * @param method   REST method path
     * @param query    query map
     * @param dataType entries type
     * @param <T>      result type
     * @return parsed entries
     */
    public <T> T delete(String method, Map<String, ?> query, JavaType dataType) {
        return execute(HttpMethod.DELETE, queryUri(methodUri(method), query), null, jsonHeaders(), dataType, false);
    }

    /**
     * DELETE that only checks {@code return.status}.
     *
     * @param method REST method path
     * @param query  query map
     */
    public void deleteEmpty(String method, Map<String, ?> query) {
        ResponseEntity<String> response = exchange(
                HttpMethod.DELETE,
                queryUri(methodUri(method), query),
                null,
                jsonHeaders(),
                false
        );
        responseParser.parseEmpty(response);
    }

    /**
     * Multipart upload. Never retried.
     *
     * @param method      REST method path
     * @param filename    file name
     * @param contentType content type
     * @param content     bytes
     * @param dataType    entries type
     * @param <T>         result type
     * @return parsed entries
     */
    public <T> T postMultipart(
            String method,
            String filename,
            MediaType contentType,
            byte[] content,
            JavaType dataType
    ) {
        if (content == null || content.length == 0) {
            throw new KavenegarValidationException("File is required");
        }
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        ByteArrayResource resource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
        org.springframework.http.HttpEntity<ByteArrayResource> filePart =
                new org.springframework.http.HttpEntity<>(resource, fileHeaders(contentType));
        body.add("File", filePart);

        HttpHeaders headers = jsonHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(
                    methodUri(method),
                    HttpMethod.POST,
                    new org.springframework.http.HttpEntity<>(body, headers),
                    String.class
            );
        } catch (RestClientException ex) {
            throw new KavenegarTransportException("Request to Kavenegar failed", sanitizeTransportCause(ex));
        }
        if (response == null) {
            throw new KavenegarTransportException("Request to Kavenegar failed", null);
        }
        return responseParser.parse(response, dataType);
    }

    private HttpHeaders fileHeaders(MediaType contentType) {
        HttpHeaders headers = new HttpHeaders();
        if (contentType != null) {
            headers.setContentType(contentType);
        }
        return headers;
    }

    private RuntimeException sanitizeTransportCause(Throwable ex) {
        if (ex == null) {
            return null;
        }
        String message = maskApiKeyInText(ex.getMessage(), config.apiKey());
        RuntimeException sanitized = new RuntimeException(message == null ? "transport failure" : message);
        sanitized.setStackTrace(ex.getStackTrace());
        return sanitized;
    }

    private URI methodUri(String method) {
        return methodUri(config.apiKey(), method);
    }

    private URI methodUri(String apiKey, String method) {
        return UriComponentsBuilder.fromUri(config.baseUrl())
                .path("/v1/{apiKey}/" + method + ".json")
                .buildAndExpand(apiKey)
                .encode()
                .toUri();
    }

    private URI queryUri(URI base, Map<String, ?> query) {
        return buildQueryUri(base, query);
    }

    private HttpHeaders jsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.USER_AGENT, config.userAgent());
        return headers;
    }

    private HttpHeaders formHeaders() {
        HttpHeaders headers = jsonHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    private <T> T execute(
            HttpMethod method,
            URI url,
            String body,
            HttpHeaders headers,
            JavaType dataType,
            boolean retryable
    ) {
        ResponseEntity<String> response = exchange(method, url, body, headers, retryable);
        return responseParser.parse(response, dataType);
    }

    private ResponseEntity<String> exchange(
            HttpMethod method,
            URI url,
            String body,
            HttpHeaders headers,
            boolean retryable
    ) {
        int attempts = (retryable && config.retryEnabled()) ? config.retryMaxAttempts() : 1;
        long backoffMillis = (retryable && config.retryEnabled()) ? config.retryBackoff().toMillis() : 0;
        RestClientException last = null;
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                ResponseEntity<String> response = restTemplate.execute(url, method, httpRequest -> {
                    httpRequest.getHeaders().putAll(headers);
                    if (body != null && !body.isBlank()) {
                        httpRequest.getBody().write(body.getBytes(StandardCharsets.UTF_8));
                    }
                }, RESPONSE_EXTRACTOR);
                if (response == null) {
                    throw new KavenegarTransportException("Request to Kavenegar failed", null);
                }
                return response;
            } catch (RestClientException ex) {
                last = ex;
                if (attempt == attempts) {
                    throw new KavenegarTransportException("Request to Kavenegar failed", sanitizeTransportCause(ex));
                }
                if (backoffMillis > 0) {
                    try {
                        Thread.sleep(backoffMillis);
                    } catch (InterruptedException interrupted) {
                        Thread.currentThread().interrupt();
                        throw new KavenegarTransportException(
                                "Request to Kavenegar failed",
                                sanitizeTransportCause(interrupted)
                        );
                    }
                }
            }
        }
        throw new KavenegarTransportException("Request to Kavenegar failed", sanitizeTransportCause(last));
    }

    private String encodeForm(Map<String, ?> form) {
        if (form == null || form.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, ?> entry : form.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            joiner.add(urlEncode(entry.getKey()) + "=" + urlEncode(String.valueOf(entry.getValue())));
        }
        return joiner.toString();
    }

}
