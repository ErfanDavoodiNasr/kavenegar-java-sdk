package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Account info from {@code account/info}.
 *
 * @param remainCredit remaining credit in Rials
 * @param expireDate   unix expiry (security field for child accounts)
 * @param type         {@code master} or {@code child}
 */
public record AccountInfo(
        @JsonProperty("remaincredit") Long remainCredit,
        @JsonProperty("expiredate") Long expireDate,
        @JsonProperty("type") String type
) {
}
