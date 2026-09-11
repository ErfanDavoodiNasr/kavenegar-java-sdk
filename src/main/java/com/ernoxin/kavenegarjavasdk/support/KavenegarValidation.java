package com.ernoxin.kavenegarjavasdk.support;

import com.ernoxin.kavenegarjavasdk.exception.KavenegarValidationException;
import lombok.experimental.UtilityClass;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Fail-fast validation helpers for Kavenegar requests and config.
 */
@UtilityClass
public class KavenegarValidation {
    private static final Pattern IRAN_MOBILE = Pattern.compile("^(\\+98|0098|98|0)?9\\d{9}$");
    private static final Pattern INTERNATIONAL = Pattern.compile("^(\\+|00)\\d{8,15}$");
    private static final Pattern LINE_NUMBER = Pattern.compile("^(\\+98|0098|98)?\\d{5,20}$");
    private static final Pattern TAG = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_-]{0,199}$");
    private static final Pattern TEMPLATE_NAME = Pattern.compile("^[A-Za-z][A-Za-z0-9-]{0,99}$");
    private static final Pattern UUID = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    /**
     * Requires a non-blank string.
     *
     * @param value value
     * @param field field name
     */
    public static void requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new KavenegarValidationException(field + " is required");
        }
    }

    /**
     * Requires a strictly positive number.
     *
     * @param value value
     * @param field field name
     */
    public static void requirePositive(long value, String field) {
        if (value <= 0) {
            throw new KavenegarValidationException(field + " must be positive");
        }
    }

    /**
     * Requires an optional integer to be positive when present.
     *
     * @param value value
     * @param field field name
     */
    public static void requirePositive(Integer value, String field) {
        if (value == null) {
            return;
        }
        if (value <= 0) {
            throw new KavenegarValidationException(field + " must be positive");
        }
    }

    /**
     * Requires {@code value <= max} when present.
     *
     * @param value value
     * @param max   inclusive max
     * @param field field name
     */
    public static void requireMax(Integer value, int max, String field) {
        if (value == null) {
            return;
        }
        if (value > max) {
            throw new KavenegarValidationException(field + " must be at most " + max);
        }
    }

    /**
     * Requires string length at most {@code max}.
     *
     * @param value value
     * @param max   max length
     * @param field field name
     */
    public static void requireMaxLength(String value, int max, String field) {
        if (value != null && value.length() > max) {
            throw new KavenegarValidationException(field + " must be at most " + max + " characters");
        }
    }

    /**
     * Requires an absolute HTTPS URI.
     *
     * @param uri   URI
     * @param field field name
     */
    public static void requireHttpsUri(URI uri, String field) {
        if (uri == null) {
            throw new KavenegarValidationException(field + " is required");
        }
        if (!uri.isAbsolute() || uri.getScheme() == null) {
            throw new KavenegarValidationException(field + " must be an absolute URL");
        }
        if (!"https".equals(uri.getScheme().toLowerCase(Locale.ROOT))) {
            throw new KavenegarValidationException(field + " must use https");
        }
    }

    /**
     * Strips trailing slashes from a base URL.
     *
     * @param uri base URL
     * @return normalized URI
     */
    public static URI normalizeBaseUrl(URI uri) {
        String value = uri.toString();
        while (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
        }
        return URI.create(value);
    }

    /**
     * Requires a non-empty list with at most {@code max} items and no null/blank entries.
     *
     * @param values values
     * @param max    max size
     * @param field  field name
     */
    public static void requireStringList(List<String> values, int max, String field) {
        if (values == null || values.isEmpty()) {
            throw new KavenegarValidationException(field + " is required");
        }
        if (values.size() > max) {
            throw new KavenegarValidationException(field + " size must be at most " + max);
        }
        for (String value : values) {
            if (value == null || value.isBlank()) {
                throw new KavenegarValidationException(field + " contains a blank value");
            }
        }
    }

    /**
     * Requires a non-empty list of ids with at most {@code max} items.
     *
     * @param values values
     * @param max    max size
     * @param field  field name
     */
    public static void requireIdList(List<Long> values, int max, String field) {
        if (values == null || values.isEmpty()) {
            throw new KavenegarValidationException(field + " is required");
        }
        if (values.size() > max) {
            throw new KavenegarValidationException(field + " size must be at most " + max);
        }
        for (Long value : values) {
            if (value == null || value <= 0) {
                throw new KavenegarValidationException(field + " contains an invalid id");
            }
        }
    }

    /**
     * Requires an Iranian mobile or an international number ({@code +} / {@code 00}).
     *
     * @param receptor receptor
     * @param field    field name
     */
    public static String requireReceptor(String receptor, String field) {
        requireNonBlank(receptor, field);
        String normalized = normalizeDigits(receptor);
        if (IRAN_MOBILE.matcher(normalized).matches() || INTERNATIONAL.matcher(normalized).matches()) {
            return normalized;
        }
        throw new KavenegarValidationException(field + " is not a valid receptor number");
    }

    /**
     * Requires a sender line in the formats documented by Kavenegar.
     *
     * @param sender sender
     * @param field  field name
     */
    public static String requireSender(String sender, String field) {
        requireNonBlank(sender, field);
        String normalized = normalizeDigits(sender);
        if (!LINE_NUMBER.matcher(normalized).matches()) {
            throw new KavenegarValidationException(field + " is not a valid sender line");
        }
        return normalized;
    }

    /**
     * Requires a optional tag name matching Kavenegar rules.
     *
     * @param tag tag
     */
    public static void requireTag(String tag) {
        if (tag == null) {
            return;
        }
        if (tag.isBlank() || !TAG.matcher(tag).matches()) {
            throw new KavenegarValidationException(
                    "tag must be 1-200 English letters/digits and may include - or _"
            );
        }
    }

    /**
     * Requires a verify template name: English, no space or underscore.
     *
     * @param name template name
     */
    public static void requireTemplateName(String name) {
        requireNonBlank(name, "template");
        if (!TEMPLATE_NAME.matcher(name.trim()).matches() || name.contains("_")) {
            throw new KavenegarValidationException("template name must be English without spaces or underscore");
        }
    }

    /**
     * Requires a verify token without spaces (token / token2 / token3).
     *
     * @param token token
     * @param field field name
     */
    public static void requireToken(String token, String field) {
        requireNonBlank(token, field);
        requireMaxLength(token, 100, field);
        if (token.contains(" ")) {
            throw new KavenegarValidationException(field + " must not contain spaces");
        }
    }

    /**
     * Requires an optional token with a limited number of spaces.
     *
     * @param token     token
     * @param field     field name
     * @param maxSpaces max spaces
     */
    public static void requireSpacedToken(String token, String field, int maxSpaces) {
        if (token == null) {
            return;
        }
        requireNonBlank(token, field);
        requireMaxLength(token, 100, field);
        long spaces = token.chars().filter(ch -> ch == ' ').count();
        if (spaces > maxSpaces) {
            throw new KavenegarValidationException(field + " may contain at most " + maxSpaces + " spaces");
        }
    }

    /**
     * Requires a UUID media id.
     *
     * @param mediaId media id
     */
    public static void requireMediaId(String mediaId) {
        requireNonBlank(mediaId, "mediaId");
        if (!UUID.matcher(mediaId.trim()).matches()) {
            throw new KavenegarValidationException("mediaId must be a UUID");
        }
    }

    /**
     * Requires unix seconds not in the past when scheduling a send.
     *
     * @param unixSeconds scheduled unix time
     */
    public static void requireFutureUnix(Long unixSeconds) {
        if (unixSeconds == null) {
            return;
        }
        if (unixSeconds <= 0) {
            throw new KavenegarValidationException("date must be a positive unix timestamp");
        }
        if (unixSeconds < Instant.now().getEpochSecond()) {
            throw new KavenegarValidationException("date must not be in the past");
        }
    }

    /**
     * Requires a unix range where end is not before start.
     *
     * @param startDate start
     * @param endDate   end, optional
     */
    public static void requireDateRange(Long startDate, Long endDate) {
        if (startDate != null && startDate <= 0) {
            throw new KavenegarValidationException("startdate must be positive");
        }
        if (endDate != null && endDate <= 0) {
            throw new KavenegarValidationException("enddate must be positive");
        }
        if (startDate != null && endDate != null && startDate > endDate) {
            throw new KavenegarValidationException("startdate must be less than or equal to enddate");
        }
    }

    /**
     * Joins values with comma as Kavenegar list parameters expect.
     *
     * @param values values
     * @return comma-separated string
     */
    public static String joinComma(List<?> values) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(values.get(i));
        }
        return builder.toString();
    }

    /**
     * Removes spaces and dashes from a phone-like string.
     *
     * @param value value
     * @return compact form
     */
    public static String normalizeDigits(String value) {
        return value.trim().replace(" ", "").replace("-", "");
    }
}
