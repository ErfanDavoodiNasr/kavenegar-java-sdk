package com.ernoxin.kavenegarjavasdk.exception;

/**
 * Indicates that SDK input or configuration validation failed before a network call.
 */
public class KavenegarValidationException extends KavenegarException {
    /**
     * Creates a validation exception with message.
     *
     * @param message validation error description
     */
    public KavenegarValidationException(String message) {
        super(message);
    }

    /**
     * Creates a validation exception with message and cause.
     *
     * @param message validation error description
     * @param cause   root cause
     */
    public KavenegarValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
