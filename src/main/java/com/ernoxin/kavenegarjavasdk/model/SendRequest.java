package com.ernoxin.kavenegarjavasdk.model;

import java.util.List;

/**
 * Simple send request ({@code sms/send}).
 *
 * @param receptors comma-joined by the client
 * @param message   SMS body
 * @param sender    sender line; {@code null} uses config default
 * @param date      unix send time; {@code null} sends immediately
 * @param type      display type 0-3; {@code null} uses API default (1)
 * @param localIds  optional local ids, same size as receptors
 * @param hide      {@code 1} hides receptors in the panel
 * @param tag       optional tag
 * @param policy    optional send policy name
 * @param mediaId   optional media UUID for internal messenger lines
 */
public record SendRequest(
        List<String> receptors,
        String message,
        String sender,
        Long date,
        Integer type,
        List<String> localIds,
        Integer hide,
        String tag,
        String policy,
        String mediaId
) {
}
