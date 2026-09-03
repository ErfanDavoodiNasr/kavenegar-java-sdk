package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Status row from {@code sms/statusbyreceptor}.
 *
 * @param messageId  message id
 * @param receptor   receptor
 * @param status     delivery status code
 * @param statusText delivery status text
 */
public record ReceptorStatusResult(
        @JsonProperty("messageid") Long messageId,
        @JsonProperty("receptor") String receptor,
        @JsonProperty("status") Integer status,
        @JsonProperty("statustext") String statusText
) {
}
