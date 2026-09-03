package com.ernoxin.kavenegarjavasdk.http;

import com.ernoxin.kavenegarjavasdk.exception.KavenegarApiException;
import com.ernoxin.kavenegarjavasdk.model.AccountInfo;
import com.ernoxin.kavenegarjavasdk.model.InboxMessage;
import com.ernoxin.kavenegarjavasdk.model.MessageResult;
import com.ernoxin.kavenegarjavasdk.model.PagedResult;
import com.ernoxin.kavenegarjavasdk.support.KavenegarObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KavenegarResponseParserTest {
    private final ObjectMapper mapper = KavenegarObjectMapper.create();
    private final KavenegarResponseParser parser = new KavenegarResponseParser(mapper);

    @Test
    void parsesSendArrayEntries() {
        String body = """
                {"return":{"status":200,"message":"تایید شد"},"entries":[
                  {"messageid":8792343,"message":"خدمات پیام کوتاه کاوه نگار","status":1,
                   "statustext":"در صف ارسال","sender":"10004346","receptor":"09121234567",
                   "date":1356619709,"cost":120}
                ]}
                """;
        List<MessageResult> rows = parser.parse(
                new ResponseEntity<>(body, HttpStatus.OK),
                mapper.getTypeFactory().constructCollectionType(List.class, MessageResult.class)
        );
        assertEquals(1, rows.size());
        assertEquals(8792343L, rows.getFirst().messageId());
        assertEquals(120, rows.getFirst().cost());
        assertEquals("در صف ارسال", rows.getFirst().statusText());
    }

    @Test
    void wrapsSingleObjectEntriesAsList() {
        String body = """
                {"return":{"status":200,"message":"تایید شد"},"entries":{
                  "messageid":8792343,"message":"ok","status":5,"statustext":"ارسال به مخابرات",
                  "sender":"10004346","receptor":"09121234567","date":1356619709,"cost":120
                }}
                """;
        List<MessageResult> rows = parser.parse(
                new ResponseEntity<>(body, HttpStatus.OK),
                mapper.getTypeFactory().constructCollectionType(List.class, MessageResult.class)
        );
        assertEquals(8792343L, rows.getFirst().messageId());
    }

    @Test
    void parsesAccountInfoObject() {
        String body = """
                {"return":{"status":200,"message":"تایید شد"},
                 "entries":{"remaincredit":1500000,"expiredate":13548889,"type":"master"}}
                """;
        AccountInfo info = parser.parse(
                new ResponseEntity<>(body, HttpStatus.OK),
                mapper.getTypeFactory().constructType(AccountInfo.class)
        );
        assertEquals(1_500_000L, info.remainCredit());
        assertEquals("master", info.type());
    }

    @Test
    void parsesPagedInboxWithStringMetadata() {
        String body = """
                {"return":{"status":200,"message":"تایید شد"},
                 "entries":[{"messageid":35850015,"message":"hi","sender":"09121234567",
                   "receptor":"3000202030","date":1357206241}],
                 "metadata":{"totalcount":"2","currentpage":"1","totalpages":"1","pagesize":"200"}}
                """;
        PagedResult<InboxMessage> page = parser.parsePaged(
                new ResponseEntity<>(body, HttpStatus.OK),
                InboxMessage.class
        );
        assertEquals(1, page.entries().size());
        assertEquals(2, page.metadata().totalCount());
        assertEquals(200, page.metadata().pageSize());
    }

    @Test
    void throwsOnLogicalStatus() {
        String body = """
                {"return":{"status":418,"message":"no credit"},"entries":null}
                """;
        KavenegarApiException ex = assertThrows(KavenegarApiException.class, () -> parser.parse(
                new ResponseEntity<>(body, HttpStatus.OK),
                mapper.getTypeFactory().constructType(AccountInfo.class)
        ));
        assertEquals(418, ex.getGatewayStatus());
        assertTrue(ex.getGatewayMessage().contains("اعتبار"));
    }

    @Test
    void throwsOnEmptyBody() {
        assertThrows(KavenegarApiException.class, () -> parser.parse(
                new ResponseEntity<>("  ", HttpStatus.OK),
                mapper.getTypeFactory().constructType(AccountInfo.class)
        ));
    }

    @Test
    void throwsWhenReturnIsSuccessButHttpIsNot() {
        String body = """
                {"return":{"status":200,"message":"تایید شد"},"entries":{"remaincredit":1,"type":"master"}}
                """;
        assertThrows(KavenegarApiException.class, () -> parser.parse(
                new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR),
                mapper.getTypeFactory().constructType(AccountInfo.class)
        ));
    }

    @Test
    void emptyArrayEntriesBecomeEmptyList() {
        String body = """
                {"return":{"status":200,"message":"تایید شد"},"entries":null}
                """;
        List<MessageResult> rows = parser.parse(
                new ResponseEntity<>(body, HttpStatus.OK),
                mapper.getTypeFactory().constructCollectionType(List.class, MessageResult.class)
        );
        assertEquals(List.of(), rows);
        assertNull(parser.parsePaged(new ResponseEntity<>(body, HttpStatus.OK), InboxMessage.class).metadata());
    }
}
