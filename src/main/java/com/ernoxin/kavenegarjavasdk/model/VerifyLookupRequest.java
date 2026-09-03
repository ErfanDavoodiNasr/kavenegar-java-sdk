package com.ernoxin.kavenegarjavasdk.model;

/**
 * Verify/OTP lookup request ({@code verify/lookup}).
 *
 * @param receptor receptor; international numbers use {@code 00} + country code
 * @param token    required token (no spaces, max 100)
 * @param token2   optional token
 * @param token3   optional token
 * @param token10  optional token, up to 5 spaces
 * @param token20  optional token, up to 8 spaces
 * @param template approved template name
 * @param type     {@code sms} or {@code call}; {@code null} defaults to sms
 * @param tag      optional tag
 */
public record VerifyLookupRequest(
        String receptor,
        String token,
        String token2,
        String token3,
        String token10,
        String token20,
        String template,
        String type,
        String tag
) {
}
