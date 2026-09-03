package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Server clock from {@code utils/getdate}.
 *
 * @param datetime display datetime
 * @param year     year
 * @param month    month
 * @param day      day
 * @param hour     hour
 * @param minute   minute
 * @param second   second
 * @param unixTime unix seconds
 */
public record ServerDate(
        @JsonProperty("datetime") String datetime,
        @JsonProperty("year") Integer year,
        @JsonProperty("month") Integer month,
        @JsonProperty("day") Integer day,
        @JsonProperty("hour") Integer hour,
        @JsonProperty("minute") Integer minute,
        @JsonProperty("second") Integer second,
        @JsonProperty("unixtime") Long unixTime
) {
}
