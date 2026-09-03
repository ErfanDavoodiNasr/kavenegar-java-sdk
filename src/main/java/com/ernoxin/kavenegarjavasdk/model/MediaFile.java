package com.ernoxin.kavenegarjavasdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Uploaded media metadata.
 *
 * @param id         media UUID
 * @param name       file name
 * @param mimeType   MIME type
 * @param extension  extension
 * @param size       size in bytes
 * @param duration   duration in seconds
 * @param resolution image/video resolution
 * @param status     processing status
 * @param statusDesc processing status text
 * @param review     review details
 */
public record MediaFile(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("mime_type") String mimeType,
        @JsonProperty("extension") String extension,
        @JsonProperty("size") Integer size,
        @JsonProperty("duration") Integer duration,
        @JsonProperty("resolution") String resolution,
        @JsonProperty("status") Integer status,
        @JsonProperty("status_desc") String statusDesc,
        @JsonProperty("review") MediaReview review
) {
}
