package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Verify template summary from list/get.
 *
 * @param id                  template id
 * @param name                template name
 * @param smsMessage          SMS body
 * @param callMessage         TTS body
 * @param primaryLineNumber   primary line
 * @param secondaryLineNumber fallback line
 * @param sendPriority        {@code SMS} or {@code Call}
 * @param switchTtl           fallback switch window
 * @param approvalStatus      approval status
 */
public record VerifyTemplate(
        @JsonProperty("id") Integer id,
        @JsonProperty("name") String name,
        @JsonProperty("smsMessage") String smsMessage,
        @JsonProperty("callMessage") String callMessage,
        @JsonProperty("primaryLineNumber") String primaryLineNumber,
        @JsonProperty("secondaryLineNumber") String secondaryLineNumber,
        @JsonProperty("sendPriority") String sendPriority,
        @JsonProperty("switchTTL") @JsonAlias("switchttL") Integer switchTtl,
        @JsonProperty("approvalStatus") String approvalStatus
) {
}
