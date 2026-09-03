package com.ernoxin.kavenegarjavasdk.model;

import java.util.List;

/**
 * TTS call request ({@code call/maketts}).
 *
 * @param receptors receptors
 * @param message   spoken text
 * @param date      unix send time
 * @param localIds  optional local ids, same size as receptors
 * @param tag       optional tag
 */
public record CallTtsRequest(
        List<String> receptors,
        String message,
        Long date,
        List<String> localIds,
        String tag
) {
}
