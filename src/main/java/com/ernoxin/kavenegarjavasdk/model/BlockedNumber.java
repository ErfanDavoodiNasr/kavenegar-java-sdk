package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Number that blocked a sender line.
 *
 * @param number      blocker number
 * @param blockReason reason code
 * @param date        unix time
 */
public record BlockedNumber(
        @JsonProperty("number") String number,
        @JsonProperty("blockreason") Integer blockReason,
        @JsonProperty("date") Long date
) {
}
