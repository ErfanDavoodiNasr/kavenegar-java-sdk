package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Compact delivery status row from {@code sms/status} and {@code sms/cancel}.
 *
 * @param messageId  message id
 * @param status     delivery status code
 * @param statusText delivery status text
 */
public record StatusResult(
        @JsonProperty("messageid") Long messageId,
        @JsonProperty("status") Integer status,
        @JsonProperty("statustext") String statusText
) {
}
