package com.ernoxin.kavenegarjavasdk.model;

/**
 * Clone an approved verify template.
 *
 * @param sourceTemplateId   source id; wins over name when both are set
 * @param sourceTemplateName source name
 * @param newTemplateName    new name
 * @param childApiKey        optional child-account API key
 * @param childLocalId       optional child-account local id
 */
public record CloneTemplateRequest(
        Integer sourceTemplateId,
        String sourceTemplateName,
        String newTemplateName,
        String childApiKey,
        String childLocalId
) {
}
