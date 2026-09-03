package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of deleting media.
 *
 * @param id      deleted id
 * @param deleted whether deletion succeeded
 */
public record MediaDeleteResult(
        @JsonProperty("id") String id,
        @JsonProperty("deleted") Boolean deleted
) {
}
