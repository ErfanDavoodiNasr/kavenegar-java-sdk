package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Media review nested object.
 *
 * @param status     review status
 * @param statusDesc review status text
 * @param reason     reason code
 * @param reasonDesc reason text
 */
public record MediaReview(
        @JsonProperty("status") Integer status,
        @JsonProperty("status_desc") String statusDesc,
        @JsonProperty("reason") Integer reason,
        @JsonProperty("reason_desc") String reasonDesc
) {
}
