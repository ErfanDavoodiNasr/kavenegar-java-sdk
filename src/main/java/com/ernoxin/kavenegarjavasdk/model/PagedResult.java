package com.ernoxin.kavenegarjavasdk.model;

import java.util.List;

/**
 * Paged API result: {@code entries} plus optional {@code metadata}.
 *
 * @param entries  page rows
 * @param metadata paging metadata, may be {@code null}
 * @param <T>      row type
 */
public record PagedResult<T>(List<T> entries, PageMetadata metadata) {
}
