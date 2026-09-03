package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Template row returned by add/update.
 *
 * @param templateId          id
 * @param name                name
 * @param sourceType          0 website, 1 app
 * @param sendMethod          1 SMS, 2 call
 * @param fallbackMethod      fallback method
 * @param primaryLineNumber   primary line
 * @param secondaryLineNumber secondary line
 * @param switchTtl           switch TTL
 * @param sourceUrl           source URL
 * @param sourceName          source name
 * @param textMessage         SMS body
 * @param voiceMessage        voice body
 */
public record VerifyTemplateDetail(
        @JsonProperty("templateid") @JsonAlias("id") String templateId,
        @JsonProperty("name") String name,
        @JsonProperty("sourcetype") String sourceType,
        @JsonProperty("sendmethod") String sendMethod,
        @JsonProperty("fallbackmethod") String fallbackMethod,
        @JsonProperty("primarylinenumber") String primaryLineNumber,
        @JsonProperty("secondarylinenumber") String secondaryLineNumber,
        @JsonProperty("switchttL") @JsonAlias("switchTTL") Integer switchTtl,
        @JsonProperty("sourceurl") String sourceUrl,
        @JsonProperty("sourcename") String sourceName,
        @JsonProperty("textmessage") String textMessage,
        @JsonProperty("voicemessage") String voiceMessage
) {
}
