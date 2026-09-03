package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Count row from {@code sms/countoutbox}.
 *
 * @param startDate range start
 * @param endDate   range end
 * @param sumPart   total SMS parts
 * @param sumCount  message count
 * @param cost      cost in Rials
 */
public record CountOutboxResult(
        @JsonProperty("startdate") Long startDate,
        @JsonProperty("enddate") Long endDate,
        @JsonProperty("sumpart") Long sumPart,
        @JsonProperty("sumcount") Long sumCount,
        @JsonProperty("cost") Long cost
) {
}
