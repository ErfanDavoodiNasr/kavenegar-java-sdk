package com.ernoxin.kavenegarjavasdk.exception;

import lombok.Getter;

/**
 * Thrown when Kavenegar returns an unsuccessful or malformed API response.
 */
@Getter
public class KavenegarApiException extends KavenegarException {
    private final int httpStatus;
    private final Integer gatewayStatus;
    private final String gatewayMessage;
    private final String rawBody;

    /**
     * Creates an API exception.
     *
     * @param httpStatus     HTTP status code
     * @param gatewayStatus  Kavenegar {@code return.status}, may be {@code null}
     * @param gatewayMessage resolved message
     * @param rawBody        raw response body
     */
    public KavenegarApiException(int httpStatus, Integer gatewayStatus, String gatewayMessage, String rawBody) {
        super(buildMessage(httpStatus, gatewayStatus, gatewayMessage));
        this.httpStatus = httpStatus;
        this.gatewayStatus = gatewayStatus;
        this.gatewayMessage = gatewayMessage;
        this.rawBody = rawBody;
    }

    /**
     * Creates an API exception with cause.
     *
     * @param httpStatus     HTTP status code
     * @param gatewayStatus  Kavenegar {@code return.status}, may be {@code null}
     * @param gatewayMessage resolved message
     * @param rawBody        raw response body
     * @param cause          root cause
     */
    public KavenegarApiException(
            int httpStatus,
            Integer gatewayStatus,
            String gatewayMessage,
            String rawBody,
            Throwable cause
    ) {
        super(buildMessage(httpStatus, gatewayStatus, gatewayMessage), cause);
        this.httpStatus = httpStatus;
        this.gatewayStatus = gatewayStatus;
        this.gatewayMessage = gatewayMessage;
        this.rawBody = rawBody;
    }

    private static String buildMessage(int httpStatus, Integer gatewayStatus, String gatewayMessage) {
        StringBuilder builder = new StringBuilder();
        builder.append("Kavenegar API error");
        builder.append(" (http ").append(httpStatus).append(")");
        if (gatewayStatus != null) {
            builder.append(" status ").append(gatewayStatus);
        }
        if (gatewayMessage != null && !gatewayMessage.isBlank()) {
            builder.append(": ").append(gatewayMessage);
        }
        return builder.toString();
    }
}
