package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Inbound SMS row.
 *
 * @param messageId inbound id
 * @param message   body
 * @param sender    originator
 * @param receptor  destination line
 * @param date      unix receive time
 */
public record InboxMessage(
        @JsonProperty("messageid") Long messageId,
        @JsonProperty("message") String message,
        @JsonProperty("sender") String sender,
        @JsonProperty("receptor") String receptor,
        @JsonProperty("date") Long date
) {
}
