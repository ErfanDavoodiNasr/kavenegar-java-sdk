package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Pagination metadata returned next to {@code entries}.
 *
 * @param totalCount  total rows
 * @param currentPage current page
 * @param totalPages  total pages
 * @param pageSize    page size
 */
public record PageMetadata(
        @JsonProperty("totalcount") Integer totalCount,
        @JsonProperty("currentpage") Integer currentPage,
        @JsonProperty("totalpages") Integer totalPages,
        @JsonProperty("pagesize") Integer pageSize
) {
}
