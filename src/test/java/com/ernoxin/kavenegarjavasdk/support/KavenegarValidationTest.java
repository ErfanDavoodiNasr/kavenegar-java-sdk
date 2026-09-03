package com.ernoxin.kavenegarjavasdk.support;

import com.ernoxin.kavenegarjavasdk.exception.KavenegarValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KavenegarValidationTest {
    @Test
    void acceptsDocumentedReceptorFormats() {
        assertDoesNotThrow(() -> KavenegarValidation.requireReceptor("09121234567", "receptor"));
        assertDoesNotThrow(() -> KavenegarValidation.requireReceptor("+989121234567", "receptor"));
        assertDoesNotThrow(() -> KavenegarValidation.requireReceptor("00989121234567", "receptor"));
        assertDoesNotThrow(() -> KavenegarValidation.requireReceptor("9121234567", "receptor"));
        assertDoesNotThrow(() -> KavenegarValidation.requireReceptor("00974211234565", "receptor"));
    }

    @Test
    void rejectsBlankAndOversizedLists() {
        assertThrows(KavenegarValidationException.class, () ->
                KavenegarValidation.requireStringList(List.of(), 200, "receptors"));
        assertThrows(KavenegarValidationException.class, () ->
                KavenegarValidation.requireStringList(List.of("a", "b", "c"), 2, "receptors"));
    }

    @Test
    void rejectsTokenSpaces() {
        assertThrows(KavenegarValidationException.class, () ->
                KavenegarValidation.requireToken("12 34", "token"));
        assertDoesNotThrow(() -> KavenegarValidation.requireSpacedToken("hello world", "token10", 5));
        assertThrows(KavenegarValidationException.class, () ->
                KavenegarValidation.requireSpacedToken("a b c d e f g", "token10", 5));
    }

    @Test
    void joinsComma() {
        assertEquals("1,2,3", KavenegarValidation.joinComma(List.of(1, 2, 3)));
    }
}
