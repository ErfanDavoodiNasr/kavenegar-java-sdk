package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Status row keyed by local id.
 *
 * @param messageId  Kavenegar message id
 * @param localId    caller local id
 * @param status     delivery status code
 * @param statusText delivery status text
 */
public record LocalStatusResult(
        @JsonProperty("messageid") Long messageId,
        @JsonProperty("localid") String localId,
        @JsonProperty("status") Integer status,
        @JsonProperty("statustext") String statusText
) {
}
