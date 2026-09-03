package com.ernoxin.kavenegarjavasdk.model;

/**
 * Optional fields for {@code account/config} updates. Null fields are omitted.
 *
 * @param apiLogs        {@code justfaults}, {@code enabled}, {@code disabled}
 * @param debugMode      {@code enabled} or {@code disabled}
 * @param defaultSender  default sender line
 * @param minCreditAlarm alarm threshold in Rials
 * @param resendFailed   {@code enabled} or {@code disabled}
 */
public record AccountConfigUpdate(
        String apiLogs,
        String debugMode,
        String defaultSender,
        Integer minCreditAlarm,
        String resendFailed
) {
}
