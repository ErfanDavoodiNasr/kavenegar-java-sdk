package com.ernoxin.kavenegarjavasdk.exception;

/**
 * Base unchecked exception type for Kavenegar SDK failures.
 */
public class KavenegarException extends RuntimeException {
    /**
     * Creates a new exception with message.
     *
     * @param message failure message
     */
    public KavenegarException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with message and cause.
     *
     * @param message failure message
     * @param cause   root cause
     */
    public KavenegarException(String message, Throwable cause) {
        super(message, cause);
    }
}
