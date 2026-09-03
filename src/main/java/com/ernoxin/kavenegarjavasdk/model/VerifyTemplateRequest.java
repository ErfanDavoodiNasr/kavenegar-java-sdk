package com.ernoxin.kavenegarjavasdk.model;

/**
 * Create or update a verify template.
 *
 * @param templateId          required for update
 * @param sourceType          0 website, 1 app
 * @param sendMethod          1 SMS, 2 call
 * @param fallBackMethod      0 default, 1 SMS, 2 call, 3 off
 * @param primaryLineNumber   primary line
 * @param secondaryLineNumber fallback line
 * @param switchTtl           1-5 when fallback is enabled
 * @param sourceUrl           website/app URL
 * @param sourceName          website/app name
 * @param name                English name without spaces or underscore
 * @param textMessage         SMS body; must include {@code %token} when SMS is used
 * @param voiceMessage        voice body; must include {@code %token} or {@code $token} when call is used
 * @param childApiKey         optional child-account API key
 * @param childLocalId        optional child-account local id
 */
public record VerifyTemplateRequest(
        Integer templateId,
        Integer sourceType,
        Integer sendMethod,
        Integer fallBackMethod,
        String primaryLineNumber,
        String secondaryLineNumber,
        Integer switchTtl,
        String sourceUrl,
        String sourceName,
        String name,
        String textMessage,
        String voiceMessage,
        String childApiKey,
        String childLocalId
) {
}
