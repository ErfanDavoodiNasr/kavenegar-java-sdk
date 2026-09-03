package com.ernoxin.kavenegarjavasdk.model;

import java.util.List;

/**
 * Array send request ({@code sms/sendarray}). Lists must be the same size.
 *
 * @param receptors       receptors
 * @param senders         sender lines
 * @param messages        message bodies
 * @param date            unix send time; {@code null} sends immediately
 * @param types           optional display types
 * @param localMessageIds optional local ids
 * @param hide            {@code 1} hides receptors in the panel
 * @param tag             optional tag
 * @param policy          optional send policy name
 * @param mediaId         optional media UUID
 */
public record SendArrayRequest(
        List<String> receptors,
        List<String> senders,
        List<String> messages,
        Long date,
        List<Integer> types,
        List<String> localMessageIds,
        Integer hide,
        String tag,
        String policy,
        String mediaId
) {
}
