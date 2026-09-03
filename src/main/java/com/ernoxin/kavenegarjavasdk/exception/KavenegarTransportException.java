package com.ernoxin.kavenegarjavasdk.exception;

/**
 * Indicates transport-level communication failure while calling Kavenegar.
 *
 * <p>This represents network/client problems (timeouts, I/O failures), not API business codes.
 */
public class KavenegarTransportException extends KavenegarException {
    /**
     * Creates a transport exception.
     *
     * @param message transport failure summary
     * @param cause   root cause
     */
    public KavenegarTransportException(String message, Throwable cause) {
        super(message, cause);
    }
}
