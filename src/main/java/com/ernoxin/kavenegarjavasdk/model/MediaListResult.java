package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Paged media list payload stored in {@code entries}.
 *
 * @param page  page number
 * @param size  page size
 * @param total total items
 * @param list  media rows
 */
public record MediaListResult(
        @JsonProperty("page") Integer page,
        @JsonProperty("size") Integer size,
        @JsonProperty("total") Long total,
        @JsonProperty("list") List<MediaFile> list
) {
}
