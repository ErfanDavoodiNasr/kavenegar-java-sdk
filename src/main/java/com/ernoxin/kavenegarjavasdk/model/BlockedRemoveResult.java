package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of {@code line/blocked/remove}.
 *
 * @param message outcome message
 */
public record BlockedRemoveResult(
        @JsonProperty("message") String message
) {
}
