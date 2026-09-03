package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Send / verify / TTS result row.
 *
 * @param messageId  unique message id
 * @param message    sent text
 * @param status     delivery status code
 * @param statusText delivery status text
 * @param sender     sender line
 * @param receptor   receptor
 * @param date       unix send time
 * @param cost       cost in Rials
 */
public record MessageResult(
        @JsonProperty("messageid") Long messageId,
        @JsonProperty("message") String message,
        @JsonProperty("status") Integer status,
        @JsonProperty("statustext") String statusText,
        @JsonProperty("sender") String sender,
        @JsonProperty("receptor") String receptor,
        @JsonProperty("date") Long date,
        @JsonProperty("cost") Integer cost
) {
}
