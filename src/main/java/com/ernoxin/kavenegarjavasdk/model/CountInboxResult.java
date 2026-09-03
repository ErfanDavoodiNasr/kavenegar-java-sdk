package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Count row from {@code sms/countinbox}.
 *
 * @param startDate range start
 * @param endDate   range end
 * @param sumCount  total count
 */
public record CountInboxResult(
        @JsonProperty("startdate") Long startDate,
        @JsonProperty("enddate") Long endDate,
        @JsonProperty("sumcount") Long sumCount
) {
}
