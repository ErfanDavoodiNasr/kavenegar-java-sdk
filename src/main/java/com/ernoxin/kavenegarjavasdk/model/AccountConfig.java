package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account config from {@code account/config}.
 *
 * @param apiLogs        {@code justfaults}, {@code enabled}, or {@code disabled}
 * @param dailyReport    daily report flag
 * @param debugMode      {@code enabled} or {@code disabled}
 * @param defaultSender  default sender line
 * @param minCreditAlarm credit alarm threshold in Rials
 * @param resendFailed   resend-failed flag
 */
public record AccountConfig(
        @JsonProperty("apilogs") String apiLogs,
        @JsonProperty("dailyreport") String dailyReport,
        @JsonProperty("debugmode") String debugMode,
        @JsonProperty("defaultsender") String defaultSender,
        @JsonProperty("mincreditalarm") Integer minCreditAlarm,
        @JsonProperty("resendfailed") String resendFailed
) {
}
