package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result of cloning a verify template.
 *
 * @param id   new template id
 * @param name new template name
 */
public record CloneTemplateResult(
        @JsonProperty("id") Integer id,
        @JsonProperty("name") String name
) {
}
