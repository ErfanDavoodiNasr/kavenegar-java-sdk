package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of adding or inspecting a blocked receptor.
 *
 * @param lineNumber sender line
 * @param receptor   mobile
 * @param status     {@code Active}, {@code AlreadyExists}, or exists-check status
 */
public record BlockedMutationResult(
        @JsonProperty("linenumber") String lineNumber,
        @JsonProperty("receptor") String receptor,
        @JsonProperty("status") String status
) {
}
