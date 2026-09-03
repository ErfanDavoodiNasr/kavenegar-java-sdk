package com.ernoxin.kavenegarjavasdk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

/**
 * Spring Boot properties bound from {@code kavenegar.*}.
 */
@Setter
@Getter
@ConfigurationProperties(prefix = "kavenegar")
public class KavenegarProperties {
    /**
     * Must be {@code true} to register SDK beans via auto-configuration.
     */
    private boolean enabled = false;
    /**
     * API key used as a URL path segment ({@code /v1/{API-KEY}/...}).
     */
    private String apiKey;
    /**
     * Default sender line for send when a request omits it.
     */
    private String defaultSender;
    /**
     * API base URL.
     */
    private URI baseUrl = KavenegarConfig.DEFAULT_BASE_URL;
    private Timeout timeout = new Timeout();
    private Retry retry = new Retry();
    private Http http = new Http();
    private int maxRecipients = KavenegarConfig.DEFAULT_MAX_RECIPIENTS;
    private int maxStatusIds = KavenegarConfig.DEFAULT_MAX_STATUS_IDS;
    private int maxMessageLength = KavenegarConfig.DEFAULT_MAX_MESSAGE_LENGTH;

    /**
     * Converts bound properties to validated runtime config.
     *
     * @return config
     */
    public KavenegarConfig toConfig() {
        Timeout timeoutValue = timeout != null ? timeout : new Timeout();
        Retry retryValue = retry != null ? retry : new Retry();
        Http httpValue = http != null ? http : new Http();
        return new KavenegarConfig(
                apiKey,
                defaultSender,
                timeoutValue.getConnect(),
                timeoutValue.getRead(),
                baseUrl,
                retryValue.isEnabled(),
                retryValue.getMaxAttempts(),
                retryValue.getBackoff(),
                httpValue.getUserAgent(),
                maxRecipients,
                maxStatusIds,
                maxMessageLength
        );
    }

    @Setter
    @Getter
    public static class Timeout {
        private Duration connect = KavenegarConfig.DEFAULT_CONNECT_TIMEOUT;
        private Duration read = KavenegarConfig.DEFAULT_READ_TIMEOUT;
    }

    @Setter
    @Getter
    public static class Retry {
        private boolean enabled = KavenegarConfig.DEFAULT_RETRY_ENABLED;
        private int maxAttempts = KavenegarConfig.DEFAULT_RETRY_MAX_ATTEMPTS;
        private Duration backoff = KavenegarConfig.DEFAULT_RETRY_BACKOFF;
    }

    @Setter
    @Getter
    public static class Http {
        private String userAgent = KavenegarConfig.DEFAULT_USER_AGENT;
    }
}
