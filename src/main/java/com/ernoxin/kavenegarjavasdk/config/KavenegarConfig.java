package com.ernoxin.kavenegarjavasdk.config;

import com.ernoxin.kavenegarjavasdk.exception.KavenegarValidationException;
import com.ernoxin.kavenegarjavasdk.support.KavenegarValidation;

import java.net.URI;
import java.time.Duration;

/**
 * Immutable runtime configuration for {@link com.ernoxin.kavenegarjavasdk.client.KavenegarClient}.
 *
 * <p>Kavenegar authenticates by placing the API key in the URL path
 * ({@code /v1/{API-KEY}/...}). There is no header alternative in the REST API.
 * This SDK still uses HTTPS only and never puts the key in query strings.
 *
 * @param apiKey           private API key used as a path segment
 * @param defaultSender    default sender line when a send request omits {@code sender}
 * @param connectTimeout   connect timeout
 * @param readTimeout      read timeout
 * @param baseUrl          API base URL
 * @param retryEnabled     retries GET/read calls only; send/verify/cancel/receive-unread never retry
 * @param retryMaxAttempts total attempts when retry is enabled
 * @param retryBackoff     delay between retry attempts
 * @param userAgent        HTTP User-Agent
 * @param maxRecipients    max receptors per send (API limit is 200)
 * @param maxStatusIds     max ids per status/select/cancel (API limit is 500)
 * @param maxMessageLength max SMS body length (API: 4000 for internal messenger lines)
 */
public record KavenegarConfig(
        String apiKey,
        String defaultSender,
        Duration connectTimeout,
        Duration readTimeout,
        URI baseUrl,
        boolean retryEnabled,
        int retryMaxAttempts,
        Duration retryBackoff,
        String userAgent,
        int maxRecipients,
        int maxStatusIds,
        int maxMessageLength
) {
    public static final Duration DEFAULT_CONNECT_TIMEOUT = Duration.ofSeconds(10);
    public static final Duration DEFAULT_READ_TIMEOUT = Duration.ofSeconds(30);
    public static final URI DEFAULT_BASE_URL = URI.create("https://api.kavenegar.com");
    public static final boolean DEFAULT_RETRY_ENABLED = false;
    public static final int DEFAULT_RETRY_MAX_ATTEMPTS = 1;
    public static final Duration DEFAULT_RETRY_BACKOFF = Duration.ZERO;
    public static final String DEFAULT_USER_AGENT = "KavenegarJavaSdk";
    public static final int DEFAULT_MAX_RECIPIENTS = 200;
    public static final int DEFAULT_MAX_STATUS_IDS = 500;
    public static final int DEFAULT_MAX_MESSAGE_LENGTH = 4000;

    public KavenegarConfig {
        if (apiKey == null || apiKey.isBlank()) {
            throw new KavenegarValidationException("apiKey is required");
        }
        apiKey = apiKey.trim();
        if (defaultSender != null && !defaultSender.isBlank()) {
            KavenegarValidation.requireSender(defaultSender, "defaultSender");
            defaultSender = defaultSender.trim();
        } else {
            defaultSender = null;
        }
        if (connectTimeout == null) {
            connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        }
        if (readTimeout == null) {
            readTimeout = DEFAULT_READ_TIMEOUT;
        }
        if (connectTimeout.isZero() || connectTimeout.isNegative()) {
            throw new KavenegarValidationException("connectTimeout must be positive");
        }
        if (readTimeout.isZero() || readTimeout.isNegative()) {
            throw new KavenegarValidationException("readTimeout must be positive");
        }
        if (baseUrl == null) {
            baseUrl = DEFAULT_BASE_URL;
        }
        KavenegarValidation.requireHttpsUri(baseUrl, "baseUrl");
        baseUrl = KavenegarValidation.normalizeBaseUrl(baseUrl);
        if (retryBackoff == null) {
            retryBackoff = DEFAULT_RETRY_BACKOFF;
        }
        if (retryMaxAttempts <= 0) {
            throw new KavenegarValidationException("retryMaxAttempts must be at least 1");
        }
        if (retryBackoff.isNegative()) {
            throw new KavenegarValidationException("retryBackoff must be non-negative");
        }
        if (userAgent == null || userAgent.isBlank()) {
            userAgent = DEFAULT_USER_AGENT;
        }
        userAgent = userAgent.trim();
        if (maxRecipients <= 0) {
            throw new KavenegarValidationException("maxRecipients must be positive");
        }
        if (maxStatusIds <= 0) {
            throw new KavenegarValidationException("maxStatusIds must be positive");
        }
        if (maxMessageLength <= 0) {
            throw new KavenegarValidationException("maxMessageLength must be positive");
        }
    }

    /**
     * Starts a builder with the required API key.
     *
     * @param apiKey API key
     * @return builder
     */
    public static Builder builder(String apiKey) {
        return new Builder(apiKey);
    }

    /**
     * Returns the normalized base URL.
     *
     * @return base URL
     */
    public URI baseUrl() {
        return KavenegarValidation.normalizeBaseUrl(baseUrl);
    }

    @Override
    public String toString() {
        return "KavenegarConfig[apiKey=***, defaultSender=" + defaultSender
                + ", connectTimeout=" + connectTimeout
                + ", readTimeout=" + readTimeout
                + ", baseUrl=" + baseUrl
                + ", retryEnabled=" + retryEnabled
                + ", retryMaxAttempts=" + retryMaxAttempts
                + ", retryBackoff=" + retryBackoff
                + ", userAgent=" + userAgent
                + ", maxRecipients=" + maxRecipients
                + ", maxStatusIds=" + maxStatusIds
                + ", maxMessageLength=" + maxMessageLength
                + "]";
    }

    /**
     * Mutable builder for {@link KavenegarConfig}.
     */
    public static final class Builder {
        private final String apiKey;
        private String defaultSender;
        private Duration connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        private Duration readTimeout = DEFAULT_READ_TIMEOUT;
        private URI baseUrl = DEFAULT_BASE_URL;
        private boolean retryEnabled = DEFAULT_RETRY_ENABLED;
        private int retryMaxAttempts = DEFAULT_RETRY_MAX_ATTEMPTS;
        private Duration retryBackoff = DEFAULT_RETRY_BACKOFF;
        private String userAgent = DEFAULT_USER_AGENT;
        private int maxRecipients = DEFAULT_MAX_RECIPIENTS;
        private int maxStatusIds = DEFAULT_MAX_STATUS_IDS;
        private int maxMessageLength = DEFAULT_MAX_MESSAGE_LENGTH;

        /**
         * Creates a builder.
         *
         * @param apiKey API key
         */
        public Builder(String apiKey) {
            this.apiKey = apiKey;
        }

        public Builder defaultSender(String defaultSender) {
            this.defaultSender = defaultSender;
            return this;
        }

        public Builder connectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder readTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder baseUrl(URI baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder retryEnabled(boolean retryEnabled) {
            this.retryEnabled = retryEnabled;
            return this;
        }

        public Builder retryMaxAttempts(int retryMaxAttempts) {
            this.retryMaxAttempts = retryMaxAttempts;
            return this;
        }

        public Builder retryBackoff(Duration retryBackoff) {
            this.retryBackoff = retryBackoff;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder maxRecipients(int maxRecipients) {
            this.maxRecipients = maxRecipients;
            return this;
        }

        public Builder maxStatusIds(int maxStatusIds) {
            this.maxStatusIds = maxStatusIds;
            return this;
        }

        public Builder maxMessageLength(int maxMessageLength) {
            this.maxMessageLength = maxMessageLength;
            return this;
        }

        /**
         * Builds a validated config.
         *
         * @return config
         */
        public KavenegarConfig build() {
            return new KavenegarConfig(
                    apiKey,
                    defaultSender,
                    connectTimeout,
                    readTimeout,
                    baseUrl,
                    retryEnabled,
                    retryMaxAttempts,
                    retryBackoff,
                    userAgent,
                    maxRecipients,
                    maxStatusIds,
                    maxMessageLength
            );
        }
    }
}
