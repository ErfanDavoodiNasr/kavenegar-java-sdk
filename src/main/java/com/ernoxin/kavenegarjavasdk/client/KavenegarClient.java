package com.ernoxin.kavenegarjavasdk.client;

import com.ernoxin.kavenegarjavasdk.config.KavenegarConfig;
import com.ernoxin.kavenegarjavasdk.exception.KavenegarValidationException;
import com.ernoxin.kavenegarjavasdk.http.KavenegarHttpClient;
import com.ernoxin.kavenegarjavasdk.model.AccountConfig;
import com.ernoxin.kavenegarjavasdk.model.AccountConfigUpdate;
import com.ernoxin.kavenegarjavasdk.model.AccountInfo;
import com.ernoxin.kavenegarjavasdk.model.BlockedMutationResult;
import com.ernoxin.kavenegarjavasdk.model.BlockedNumber;
import com.ernoxin.kavenegarjavasdk.model.BlockedRemoveResult;
import com.ernoxin.kavenegarjavasdk.model.CallTtsRequest;
import com.ernoxin.kavenegarjavasdk.model.CloneTemplateRequest;
import com.ernoxin.kavenegarjavasdk.model.CloneTemplateResult;
import com.ernoxin.kavenegarjavasdk.model.CountInboxResult;
import com.ernoxin.kavenegarjavasdk.model.CountOutboxResult;
import com.ernoxin.kavenegarjavasdk.model.InboxMessage;
import com.ernoxin.kavenegarjavasdk.model.LocalStatusResult;
import com.ernoxin.kavenegarjavasdk.model.MediaDeleteResult;
import com.ernoxin.kavenegarjavasdk.model.MediaFile;
import com.ernoxin.kavenegarjavasdk.model.MediaListResult;
import com.ernoxin.kavenegarjavasdk.model.MessageResult;
import com.ernoxin.kavenegarjavasdk.model.PagedResult;
import com.ernoxin.kavenegarjavasdk.model.ReceptorStatusResult;
import com.ernoxin.kavenegarjavasdk.model.SendArrayRequest;
import com.ernoxin.kavenegarjavasdk.model.SendRequest;
import com.ernoxin.kavenegarjavasdk.model.ServerDate;
import com.ernoxin.kavenegarjavasdk.model.StatusResult;
import com.ernoxin.kavenegarjavasdk.model.VerifyLookupRequest;
import com.ernoxin.kavenegarjavasdk.model.VerifyTemplate;
import com.ernoxin.kavenegarjavasdk.model.VerifyTemplateDetail;
import com.ernoxin.kavenegarjavasdk.model.VerifyTemplateRequest;
import com.ernoxin.kavenegarjavasdk.support.KavenegarEndpoints;
import com.ernoxin.kavenegarjavasdk.support.KavenegarObjectMapper;
import com.ernoxin.kavenegarjavasdk.support.KavenegarValidation;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * High-level synchronous client for Kavenegar REST.
 *
 * <p>Thread-safe after construction. Mutating send/verify/cancel/config/template/media/block
 * calls are never retried. Fetching unread inbox ({@code isread=0}) is also never retried
 * because those messages are marked read.
 */
public final class KavenegarClient {
    private final KavenegarConfig config;
    private final KavenegarHttpClient httpClient;
    private final ObjectMapper mapper;
    private final JavaType messageListType;
    private final JavaType statusListType;
    private final JavaType localStatusListType;
    private final JavaType receptorStatusListType;
    private final JavaType inboxListType;
    private final JavaType countOutboxListType;
    private final JavaType countInboxListType;
    private final JavaType blockedMutationListType;
    private final JavaType blockedRemoveType;
    private final JavaType accountInfoType;
    private final JavaType accountConfigType;
    private final JavaType serverDateType;
    private final JavaType cloneTemplateType;
    private final JavaType templateDetailType;
    private final JavaType templateType;
    private final JavaType mediaType;
    private final JavaType mediaListType;
    private final JavaType mediaDeleteType;

    /**
     * Creates a client with the default HTTP transport.
     *
     * @param config validated config
     */
    public KavenegarClient(KavenegarConfig config) {
        this(config, KavenegarHttpClient.create(config));
    }

    /**
     * Creates a client with an explicit HTTP transport.
     *
     * @param config     config
     * @param httpClient HTTP client
     */
    public KavenegarClient(KavenegarConfig config, KavenegarHttpClient httpClient) {
        if (config == null) {
            throw new KavenegarValidationException("config is required");
        }
        if (httpClient == null) {
            throw new KavenegarValidationException("httpClient is required");
        }
        this.config = config;
        this.httpClient = httpClient;
        this.mapper = KavenegarObjectMapper.create();
        this.messageListType = mapper.getTypeFactory().constructCollectionType(List.class, MessageResult.class);
        this.statusListType = mapper.getTypeFactory().constructCollectionType(List.class, StatusResult.class);
        this.localStatusListType = mapper.getTypeFactory().constructCollectionType(List.class, LocalStatusResult.class);
        this.receptorStatusListType = mapper.getTypeFactory()
                .constructCollectionType(List.class, ReceptorStatusResult.class);
        this.inboxListType = mapper.getTypeFactory().constructCollectionType(List.class, InboxMessage.class);
        this.countOutboxListType = mapper.getTypeFactory().constructCollectionType(List.class, CountOutboxResult.class);
        this.countInboxListType = mapper.getTypeFactory().constructCollectionType(List.class, CountInboxResult.class);
        this.blockedMutationListType = mapper.getTypeFactory()
                .constructCollectionType(List.class, BlockedMutationResult.class);
        this.blockedRemoveType = mapper.getTypeFactory().constructType(BlockedRemoveResult.class);
        this.accountInfoType = mapper.getTypeFactory().constructType(AccountInfo.class);
        this.accountConfigType = mapper.getTypeFactory().constructType(AccountConfig.class);
        this.serverDateType = mapper.getTypeFactory().constructType(ServerDate.class);
        this.cloneTemplateType = mapper.getTypeFactory().constructType(CloneTemplateResult.class);
        this.templateDetailType = mapper.getTypeFactory().constructType(VerifyTemplateDetail.class);
        this.templateType = mapper.getTypeFactory().constructType(VerifyTemplate.class);
        this.mediaType = mapper.getTypeFactory().constructType(MediaFile.class);
        this.mediaListType = mapper.getTypeFactory().constructType(MediaListResult.class);
        this.mediaDeleteType = mapper.getTypeFactory().constructType(MediaDeleteResult.class);
    }

    /**
     * Sends one message to one or more receptors.
     *
     * @param request send request
     * @return send rows
     */
    public List<MessageResult> send(SendRequest request) {
        if (request == null) {
            throw new KavenegarValidationException("send request is required");
        }
        KavenegarValidation.requireStringList(request.receptors(), config.maxRecipients(), "receptors");
        List<String> normalizedReceptors = new ArrayList<>();
        for (String receptor : request.receptors()) {
            normalizedReceptors.add(KavenegarValidation.requireReceptor(receptor, "receptors"));
        }
        KavenegarValidation.requireNonBlank(request.message(), "message");
        KavenegarValidation.requireMaxLength(request.message(), config.maxMessageLength(), "message");
        String sender = resolveSender(request.sender());
        KavenegarValidation.requireFutureUnix(request.date());
        requireType(request.type());
        requireHide(request.hide());
        KavenegarValidation.requireTag(request.tag());
        if (request.mediaId() != null) {
            KavenegarValidation.requireMediaId(request.mediaId());
        }
        if (request.localIds() != null) {
            KavenegarValidation.requireStringList(request.localIds(), config.maxRecipients(), "localid");
            if (request.localIds().size() != request.receptors().size()) {
                throw new KavenegarValidationException("localid size must match receptors");
            }
        }
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("receptor", KavenegarValidation.joinComma(normalizedReceptors));
        form.put("message", request.message());
        putIfPresent(form, "sender", sender);
        putIfPresent(form, "date", request.date());
        putIfPresent(form, "type", request.type());
        if (request.localIds() != null) {
            form.put("localid", KavenegarValidation.joinComma(request.localIds()));
        }
        putIfPresent(form, "hide", request.hide());
        putIfPresent(form, "tag", request.tag());
        putIfPresent(form, "policy", request.policy());
        putIfPresent(form, "mediaid", request.mediaId());
        return httpClient.postForm(KavenegarEndpoints.SMS_SEND, form, messageListType);
    }

    /**
     * Sends paired senders, receptors, and messages. POST only.
     *
     * @param request array send request
     * @return send rows
     */
    public List<MessageResult> sendArray(SendArrayRequest request) {
        if (request == null) {
            throw new KavenegarValidationException("sendArray request is required");
        }
        KavenegarValidation.requireStringList(request.receptors(), config.maxRecipients(), "receptor");
        KavenegarValidation.requireStringList(request.senders(), config.maxRecipients(), "sender");
        KavenegarValidation.requireStringList(request.messages(), config.maxRecipients(), "message");
        if (request.receptors().size() != request.senders().size()
                || request.receptors().size() != request.messages().size()) {
            throw new KavenegarValidationException("receptor, sender, and message lists must be the same size");
        }
        List<String> normalizedReceptors = new ArrayList<>();
        for (String receptor : request.receptors()) {
            normalizedReceptors.add(KavenegarValidation.requireReceptor(receptor, "receptor"));
        }
        List<String> normalizedSenders = new ArrayList<>();
        for (String sender : request.senders()) {
            normalizedSenders.add(KavenegarValidation.requireSender(sender, "sender"));
        }
        for (String message : request.messages()) {
            KavenegarValidation.requireMaxLength(message, config.maxMessageLength(), "message");
        }
        KavenegarValidation.requireFutureUnix(request.date());
        requireHide(request.hide());
        KavenegarValidation.requireTag(request.tag());
        if (request.mediaId() != null) {
            KavenegarValidation.requireMediaId(request.mediaId());
        }
        if (request.types() != null) {
            if (request.types().size() != request.receptors().size()) {
                throw new KavenegarValidationException("type size must match receptors");
            }
            for (Integer type : request.types()) {
                requireType(type);
            }
        }
        if (request.localMessageIds() != null) {
            KavenegarValidation.requireStringList(request.localMessageIds(), config.maxRecipients(), "localmessageids");
            if (request.localMessageIds().size() != request.receptors().size()) {
                throw new KavenegarValidationException("localmessageids size must match receptors");
            }
        }
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("receptor", writeJson(normalizedReceptors));
        form.put("sender", writeJson(normalizedSenders));
        form.put("message", writeJson(request.messages()));
        putIfPresent(form, "date", request.date());
        if (request.types() != null) {
            form.put("type", writeJson(request.types()));
        }
        if (request.localMessageIds() != null) {
            form.put("localmessageids", writeJson(request.localMessageIds()));
        }
        putIfPresent(form, "hide", request.hide());
        putIfPresent(form, "tag", request.tag());
        putIfPresent(form, "policy", request.policy());
        putIfPresent(form, "mediaid", request.mediaId());
        return httpClient.postForm(KavenegarEndpoints.SMS_SEND_ARRAY, form, messageListType);
    }

    /**
     * Delivery status for up to 500 message ids (48 hours).
     *
     * @param messageIds message ids
     * @return status rows
     */
    public List<StatusResult> status(List<Long> messageIds) {
        KavenegarValidation.requireIdList(messageIds, config.maxStatusIds(), "messageid");
        return httpClient.get(
                KavenegarEndpoints.SMS_STATUS,
                Map.of("messageid", KavenegarValidation.joinComma(messageIds)),
                statusListType,
                true
        );
    }

    /**
     * Delivery status by local ids (about 12 hours).
     *
     * @param localIds local ids
     * @return status rows
     */
    public List<LocalStatusResult> statusByLocalId(List<String> localIds) {
        KavenegarValidation.requireStringList(localIds, config.maxStatusIds(), "localid");
        return httpClient.get(
                KavenegarEndpoints.SMS_STATUS_LOCAL,
                Map.of("localid", KavenegarValidation.joinComma(localIds)),
                localStatusListType,
                true
        );
    }

    /**
     * Delivery status list for one receptor. Range is at most one day.
     *
     * @param receptor  receptor
     * @param startDate unix start
     * @param endDate   unix end; optional
     * @return status rows
     */
    public List<ReceptorStatusResult> statusByReceptor(String receptor, long startDate, Long endDate) {
        receptor = KavenegarValidation.requireReceptor(receptor, "receptor");
        KavenegarValidation.requirePositive(startDate, "startdate");
        KavenegarValidation.requireDateRange(startDate, endDate);
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("receptor", receptor);
        query.put("startdate", startDate);
        putIfPresent(query, "enddate", endDate);
        return httpClient.get(KavenegarEndpoints.SMS_STATUS_BY_RECEPTOR, query, receptorStatusListType, true);
    }

    /**
     * Full message details by id. Requires a allowed IP in the Kavenegar panel.
     *
     * @param messageIds message ids
     * @return message rows
     */
    public List<MessageResult> select(List<Long> messageIds) {
        KavenegarValidation.requireIdList(messageIds, config.maxStatusIds(), "messageid");
        return httpClient.get(
                KavenegarEndpoints.SMS_SELECT,
                Map.of("messageid", KavenegarValidation.joinComma(messageIds)),
                messageListType,
                true
        );
    }

    /**
     * Outbox list in a date range (max 1 day; start at most 4 days ago). Requires allowed IP.
     *
     * @param startDate unix start
     * @param endDate   unix end; optional
     * @param sender    optional sender filter
     * @return message rows
     */
    public List<MessageResult> selectOutbox(long startDate, Long endDate, String sender) {
        KavenegarValidation.requirePositive(startDate, "startdate");
        KavenegarValidation.requireDateRange(startDate, endDate);
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("startdate", startDate);
        putIfPresent(query, "enddate", endDate);
        putIfPresent(query, "sender", sender);
        return httpClient.get(KavenegarEndpoints.SMS_SELECT_OUTBOX, query, messageListType, true);
    }

    /**
     * Latest outbox rows. Requires allowed IP. {@code pagesize} max is 200.
     *
     * @param pageSize page size; optional
     * @param sender   optional sender filter
     * @return message rows
     */
    public List<MessageResult> latestOutbox(Integer pageSize, String sender) {
        KavenegarValidation.requirePositive(pageSize, "pagesize");
        KavenegarValidation.requireMax(pageSize, 200, "pagesize");
        Map<String, Object> query = new LinkedHashMap<>();
        putIfPresent(query, "pagesize", pageSize);
        putIfPresent(query, "sender", sender);
        return httpClient.get(KavenegarEndpoints.SMS_LATEST_OUTBOX, query, messageListType, true);
    }

    /**
     * Outbox counts in a date range (max 1 day).
     *
     * @param startDate unix start
     * @param endDate   unix end; optional
     * @param status    optional delivery status filter
     * @return first count row
     */
    public CountOutboxResult countOutbox(long startDate, Long endDate, Integer status) {
        KavenegarValidation.requirePositive(startDate, "startdate");
        KavenegarValidation.requireDateRange(startDate, endDate);
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("startdate", startDate);
        putIfPresent(query, "enddate", endDate);
        putIfPresent(query, "status", status);
        List<CountOutboxResult> rows = httpClient.get(
                KavenegarEndpoints.SMS_COUNT_OUTBOX,
                query,
                countOutboxListType,
                true
        );
        return first(rows, "countoutbox");
    }

    /**
     * Cancels scheduled messages. Never retried.
     *
     * @param messageIds message ids
     * @return status rows
     */
    public List<StatusResult> cancel(List<Long> messageIds) {
        KavenegarValidation.requireIdList(messageIds, config.maxStatusIds(), "messageid");
        Map<String, Object> form = Map.of("messageid", KavenegarValidation.joinComma(messageIds));
        return httpClient.postForm(KavenegarEndpoints.SMS_CANCEL, form, statusListType);
    }

    /**
     * Fetches inbox for a line. Unread ({@code unread=true}) marks messages read and is never retried.
     *
     * @param lineNumber sender line
     * @param unread     {@code true} for {@code isread=0}
     * @return inbox rows (up to 100)
     */
    public List<InboxMessage> receive(String lineNumber, boolean unread) {
        lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("linenumber", lineNumber);
        query.put("isread", unread ? 0 : 1);
        return httpClient.get(KavenegarEndpoints.SMS_RECEIVE, query, inboxListType, !unread);
    }

    /**
     * Paged inbox. Unread pages are never retried.
     *
     * @param lineNumber sender line
     * @param unread     {@code true} for {@code isread=0}
     * @param startDate  optional unix start
     * @param endDate    optional unix end
     * @param pageNumber optional page
     * @return paged inbox
     */
    public PagedResult<InboxMessage> inboxPaged(
            String lineNumber,
            boolean unread,
            Long startDate,
            Long endDate,
            Integer pageNumber
    ) {
        lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        KavenegarValidation.requireDateRange(startDate, endDate);
        KavenegarValidation.requirePositive(pageNumber, "pagenumber");
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("linenumber", lineNumber);
        query.put("isread", unread ? 0 : 1);
        putIfPresent(query, "startdate", startDate);
        putIfPresent(query, "enddate", endDate);
        putIfPresent(query, "pagenumber", pageNumber);
        return httpClient.getPaged(KavenegarEndpoints.SMS_INBOX_PAGED, query, InboxMessage.class, !unread);
    }

    /**
     * Inbox counts in a date range (max 1 day).
     *
     * @param startDate  unix start
     * @param endDate    unix end; optional
     * @param lineNumber optional line
     * @param unread     optional unread filter; {@code null} skips {@code isread}
     * @return first count row
     */
    public CountInboxResult countInbox(long startDate, Long endDate, String lineNumber, Boolean unread) {
        KavenegarValidation.requirePositive(startDate, "startdate");
        KavenegarValidation.requireDateRange(startDate, endDate);
        if (lineNumber != null) {
            lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        }
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("startdate", startDate);
        putIfPresent(query, "enddate", endDate);
        putIfPresent(query, "linenumber", lineNumber);
        if (unread != null) {
            query.put("isread", unread ? 0 : 1);
        }
        List<CountInboxResult> rows = httpClient.get(
                KavenegarEndpoints.SMS_COUNT_INBOX,
                query,
                countInboxListType,
                true
        );
        return first(rows, "countinbox");
    }

    /**
     * Lists numbers that blocked a line.
     *
     * @param lineNumber  sender line
     * @param blockReason optional reason filter
     * @param startDate   optional unix start
     * @param pageNumber  optional page
     * @return paged blocked numbers
     */
    public PagedResult<BlockedNumber> listBlocked(
            String lineNumber,
            Integer blockReason,
            Long startDate,
            Integer pageNumber
    ) {
        lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        KavenegarValidation.requirePositive(pageNumber, "pagenumber");
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("linenumber", lineNumber);
        putIfPresent(query, "blockreason", blockReason);
        putIfPresent(query, "startdate", startDate);
        putIfPresent(query, "pagenumber", pageNumber);
        return httpClient.getPaged(KavenegarEndpoints.LINE_BLOCKED_LIST, query, BlockedNumber.class, true);
    }

    /**
     * Adds receptors to a line block list. Never retried.
     *
     * @param lineNumber sender line
     * @param receptors  receptors
     * @return mutation rows
     */
    public List<BlockedMutationResult> addBlocked(String lineNumber, List<String> receptors) {
        lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        KavenegarValidation.requireStringList(receptors, config.maxRecipients(), "receptor");
        List<String> normalizedReceptors = new ArrayList<>();
        for (String receptor : receptors) {
            normalizedReceptors.add(KavenegarValidation.requireReceptor(receptor, "receptor"));
        }
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("linenumber", lineNumber);
        form.put("receptor", KavenegarValidation.joinComma(normalizedReceptors));
        return httpClient.postForm(KavenegarEndpoints.LINE_BLOCKED_ADD, form, blockedMutationListType);
    }

    /**
     * Checks whether receptors are blocked on a line.
     *
     * @param lineNumber sender line
     * @param receptors  receptors
     * @return exists rows
     */
    public List<BlockedMutationResult> blockedExists(String lineNumber, List<String> receptors) {
        lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        KavenegarValidation.requireStringList(receptors, config.maxRecipients(), "receptor");
        List<String> normalizedReceptors = new ArrayList<>();
        for (String receptor : receptors) {
            normalizedReceptors.add(KavenegarValidation.requireReceptor(receptor, "receptor"));
        }
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("linenumber", lineNumber);
        query.put("receptor", KavenegarValidation.joinComma(normalizedReceptors));
        return httpClient.get(KavenegarEndpoints.LINE_BLOCKED_EXISTS, query, blockedMutationListType, true);
    }

    /**
     * Removes receptors from a line block list. Never retried.
     *
     * @param lineNumber sender line
     * @param receptors  receptors
     * @return remove result
     */
    public BlockedRemoveResult removeBlocked(String lineNumber, List<String> receptors) {
        lineNumber = KavenegarValidation.requireSender(lineNumber, "linenumber");
        KavenegarValidation.requireStringList(receptors, config.maxRecipients(), "receptor");
        List<String> normalizedReceptors = new ArrayList<>();
        for (String receptor : receptors) {
            normalizedReceptors.add(KavenegarValidation.requireReceptor(receptor, "receptor"));
        }
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("linenumber", lineNumber);
        query.put("receptor", KavenegarValidation.joinComma(normalizedReceptors));
        return httpClient.delete(KavenegarEndpoints.LINE_BLOCKED_REMOVE, query, blockedRemoveType);
    }

    /**
     * High-priority verify/OTP template send. Never retried. Sent as POST so tokens stay out of the query string.
     *
     * @param request lookup request
     * @return send row
     */
    public MessageResult verifyLookup(VerifyLookupRequest request) {
        if (request == null) {
            throw new KavenegarValidationException("verify request is required");
        }
        String receptor = KavenegarValidation.requireReceptor(request.receptor(), "receptor");
        KavenegarValidation.requireToken(request.token(), "token");
        if (request.token2() != null) {
            KavenegarValidation.requireToken(request.token2(), "token2");
        }
        if (request.token3() != null) {
            KavenegarValidation.requireToken(request.token3(), "token3");
        }
        KavenegarValidation.requireSpacedToken(request.token10(), "token10", 5);
        KavenegarValidation.requireSpacedToken(request.token20(), "token20", 8);
        KavenegarValidation.requireTemplateName(request.template());
        KavenegarValidation.requireTag(request.tag());
        if (request.type() != null) {
            String type = request.type().trim().toLowerCase(Locale.ROOT);
            if (!type.equals("sms") && !type.equals("call")) {
                throw new KavenegarValidationException("type must be sms or call");
            }
        }
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("receptor", receptor);
        form.put("token", request.token());
        putIfPresent(form, "token2", request.token2());
        putIfPresent(form, "token3", request.token3());
        putIfPresent(form, "token10", request.token10());
        putIfPresent(form, "token20", request.token20());
        form.put("template", request.template());
        putIfPresent(form, "type", request.type());
        putIfPresent(form, "tag", request.tag());
        List<MessageResult> rows = httpClient.postForm(KavenegarEndpoints.VERIFY_LOOKUP, form, messageListType);
        return first(rows, "verify/lookup");
    }

    /**
     * Lists verify templates.
     *
     * @param page         optional page
     * @param childApiKey  optional child API key
     * @param childLocalId optional child local id
     * @return paged templates
     */
    public PagedResult<VerifyTemplate> listTemplates(Integer page, String childApiKey, String childLocalId) {
        KavenegarValidation.requirePositive(page, "page");
        Map<String, Object> query = childQuery(childApiKey, childLocalId);
        putIfPresent(query, "page", page);
        return httpClient.getPaged(KavenegarEndpoints.VERIFY_TEMPLATE_LIST, query, VerifyTemplate.class, true);
    }

    /**
     * Clones an approved template. Never retried.
     *
     * @param request clone request
     * @return new template
     */
    public CloneTemplateResult cloneTemplate(CloneTemplateRequest request) {
        if (request == null) {
            throw new KavenegarValidationException("clone request is required");
        }
        KavenegarValidation.requireTemplateName(request.newTemplateName());
        if (request.sourceTemplateId() == null && (request.sourceTemplateName() == null
                || request.sourceTemplateName().isBlank())) {
            throw new KavenegarValidationException("sourceTemplateId or sourceTemplateName is required");
        }
        Map<String, Object> form = childForm(request.childApiKey(), request.childLocalId());
        putIfPresent(form, "sourceTemplateId", request.sourceTemplateId());
        putIfPresent(form, "sourceTemplateName", request.sourceTemplateName());
        form.put("newTemplateName", request.newTemplateName());
        return httpClient.postForm(KavenegarEndpoints.VERIFY_CLONE_TEMPLATE, form, cloneTemplateType);
    }

    /**
     * Creates a verify template. Never retried.
     *
     * @param request template request
     * @return created template
     */
    public VerifyTemplateDetail addTemplate(VerifyTemplateRequest request) {
        return postTemplate(KavenegarEndpoints.VERIFY_ADD_TEMPLATE, request, false);
    }

    /**
     * Updates a verify template. Never retried.
     *
     * @param request template request with {@code templateId}
     * @return updated template
     */
    public VerifyTemplateDetail updateTemplate(VerifyTemplateRequest request) {
        return postTemplate(KavenegarEndpoints.VERIFY_UPDATE_TEMPLATE, request, true);
    }

    /**
     * Loads one verify template.
     *
     * @param id           template id
     * @param childApiKey  optional child API key
     * @param childLocalId optional child local id
     * @return template
     */
    public VerifyTemplate getTemplate(int id, String childApiKey, String childLocalId) {
        KavenegarValidation.requirePositive(id, "id");
        Map<String, Object> query = childQuery(childApiKey, childLocalId);
        query.put("id", id);
        return httpClient.get(KavenegarEndpoints.VERIFY_GET_TEMPLATE, query, templateType, true);
    }

    /**
     * Deletes a verify template. Never retried.
     *
     * @param id           template id
     * @param childApiKey  optional child API key
     * @param childLocalId optional child local id
     */
    public void deleteTemplate(int id, String childApiKey, String childLocalId) {
        KavenegarValidation.requirePositive(id, "id");
        Map<String, Object> query = childQuery(childApiKey, childLocalId);
        query.put("id", id);
        httpClient.deleteEmpty(KavenegarEndpoints.VERIFY_DELETE_TEMPLATE, query);
    }

    /**
     * Places a TTS call. Never retried. {@code repeat} is documented as disabled and is not sent.
     *
     * @param request TTS request
     * @return send rows
     */
    public List<MessageResult> callTts(CallTtsRequest request) {
        if (request == null) {
            throw new KavenegarValidationException("TTS request is required");
        }
        KavenegarValidation.requireStringList(request.receptors(), config.maxRecipients(), "receptor");
        List<String> normalizedReceptors = new ArrayList<>();
        for (String receptor : request.receptors()) {
            normalizedReceptors.add(KavenegarValidation.requireReceptor(receptor, "receptor"));
        }
        KavenegarValidation.requireNonBlank(request.message(), "message");
        KavenegarValidation.requireFutureUnix(request.date());
        KavenegarValidation.requireTag(request.tag());
        if (request.localIds() != null) {
            KavenegarValidation.requireStringList(request.localIds(), config.maxRecipients(), "localid");
            if (request.localIds().size() != request.receptors().size()) {
                throw new KavenegarValidationException("localid size must match receptors");
            }
        }
        Map<String, Object> form = new LinkedHashMap<>();
        form.put("receptor", KavenegarValidation.joinComma(normalizedReceptors));
        form.put("message", request.message());
        putIfPresent(form, "date", request.date());
        if (request.localIds() != null) {
            form.put("localid", KavenegarValidation.joinComma(request.localIds()));
        }
        putIfPresent(form, "tag", request.tag());
        return httpClient.postForm(KavenegarEndpoints.CALL_MAKE_TTS, form, messageListType);
    }

    /**
     * Account credit and type.
     *
     * @return account info
     */
    public AccountInfo accountInfo() {
        return httpClient.get(KavenegarEndpoints.ACCOUNT_INFO, null, accountInfoType, true);
    }

    /**
     * Current account config.
     *
     * @return config
     */
    public AccountConfig getAccountConfig() {
        return httpClient.get(KavenegarEndpoints.ACCOUNT_CONFIG, null, accountConfigType, true);
    }

    /**
     * Updates account config. Never retried. Empty update is rejected.
     *
     * @param update fields to set
     * @return updated config
     */
    public AccountConfig updateAccountConfig(AccountConfigUpdate update) {
        if (update == null) {
            throw new KavenegarValidationException("config update is required");
        }
        Map<String, Object> form = new LinkedHashMap<>();
        putIfPresent(form, "apilogs", update.apiLogs());
        putIfPresent(form, "debugmode", update.debugMode());
        putIfPresent(form, "defaultsender", update.defaultSender());
        putIfPresent(form, "mincreditalarm", update.minCreditAlarm());
        putIfPresent(form, "resendfailed", update.resendFailed());
        if (form.isEmpty()) {
            throw new KavenegarValidationException("at least one config field is required");
        }
        return httpClient.postForm(KavenegarEndpoints.ACCOUNT_CONFIG, form, accountConfigType);
    }

    /**
     * Uploads media for internal messenger lines. Never retried.
     *
     * @param filename    file name
     * @param contentType MIME type
     * @param content     bytes
     * @return media metadata
     */
    public MediaFile uploadMedia(String filename, String contentType, byte[] content) {
        KavenegarValidation.requireNonBlank(filename, "filename");
        KavenegarValidation.requireNonBlank(contentType, "contentType");
        if (content == null || content.length == 0) {
            throw new KavenegarValidationException("File is required");
        }
        return httpClient.postMultipart(
                KavenegarEndpoints.MEDIA_UPLOAD,
                filename,
                MediaType.parseMediaType(contentType),
                content,
                mediaType
        );
    }

    /**
     * Lists uploaded media.
     *
     * @param page page number
     * @param size page size
     * @return media list payload
     */
    public MediaListResult listMedia(Integer page, Integer size) {
        KavenegarValidation.requirePositive(page, "page");
        KavenegarValidation.requirePositive(size, "size");
        Map<String, Object> query = new LinkedHashMap<>();
        putIfPresent(query, "page", page);
        putIfPresent(query, "size", size);
        return httpClient.get(KavenegarEndpoints.MEDIA_LIST, query, mediaListType, true);
    }

    /**
     * Loads one media file.
     *
     * @param mediaId UUID
     * @return media
     */
    public MediaFile getMedia(String mediaId) {
        KavenegarValidation.requireMediaId(mediaId);
        return httpClient.get(KavenegarEndpoints.MEDIA_GET, Map.of("id", mediaId.trim()), mediaType, true);
    }

    /**
     * Deletes media. Never retried.
     *
     * @param mediaId UUID
     * @return delete result
     */
    public MediaDeleteResult deleteMedia(String mediaId) {
        KavenegarValidation.requireMediaId(mediaId);
        return httpClient.delete(KavenegarEndpoints.MEDIA_DELETE, Map.of("id", mediaId.trim()), mediaDeleteType);
    }

    /**
     * Server date from {@code /v1/0/utils/getdate.json}. Does not use the account API key.
     *
     * @return server date
     */
    public ServerDate getServerDate() {
        return httpClient.getPublic(KavenegarEndpoints.UTILS_GET_DATE, serverDateType, true);
    }

    private VerifyTemplateDetail postTemplate(String method, VerifyTemplateRequest request, boolean update) {
        if (request == null) {
            throw new KavenegarValidationException("template request is required");
        }
        if (update) {
            if (request.templateId() == null) {
                throw new KavenegarValidationException("templateId is required");
            }
            KavenegarValidation.requirePositive(request.templateId(), "templateId");
        }
        if (request.sourceType() == null || (request.sourceType() != 0 && request.sourceType() != 1)) {
            throw new KavenegarValidationException("sourceType must be 0 or 1");
        }
        if (request.sendMethod() == null || (request.sendMethod() != 1 && request.sendMethod() != 2)) {
            throw new KavenegarValidationException("sendMethod must be 1 or 2");
        }
        KavenegarValidation.requireTemplateName(request.name());
        Integer fallback = request.fallBackMethod() == null ? 3 : request.fallBackMethod();
        if (fallback < 0 || fallback > 3) {
            throw new KavenegarValidationException("fallBackMethod must be between 0 and 3");
        }
        if (fallback != 3) {
            if (request.switchTtl() == null || request.switchTtl() < 1 || request.switchTtl() > 5) {
                throw new KavenegarValidationException("switchTTL must be between 1 and 5 when fallback is enabled");
            }
        }
        boolean sms = request.sendMethod() == 1 || fallback == 1;
        boolean call = request.sendMethod() == 2 || fallback == 2;
        if (sms) {
            KavenegarValidation.requireNonBlank(request.textMessage(), "textMessage");
            if (!request.textMessage().contains("%token")) {
                throw new KavenegarValidationException("textMessage must contain %token");
            }
        }
        if (call) {
            KavenegarValidation.requireNonBlank(request.voiceMessage(), "voiceMessage");
            if (!request.voiceMessage().contains("%token") && !request.voiceMessage().contains("$token")) {
                throw new KavenegarValidationException("voiceMessage must contain %token or $token");
            }
        }
        Map<String, Object> form = childForm(request.childApiKey(), request.childLocalId());
        if (update) {
            form.put("templateId", request.templateId());
        }
        form.put("sourceType", request.sourceType());
        form.put("sendMethod", request.sendMethod());
        form.put("fallBackMethod", fallback);
        putIfPresent(form, "primaryLineNumber", request.primaryLineNumber());
        putIfPresent(form, "secondaryLineNumber", request.secondaryLineNumber());
        putIfPresent(form, "switchTTL", request.switchTtl());
        putIfPresent(form, "sourceUrl", request.sourceUrl());
        putIfPresent(form, "sourceName", request.sourceName());
        form.put("name", request.name());
        putIfPresent(form, "textMessage", request.textMessage());
        putIfPresent(form, "voiceMessage", request.voiceMessage());
        return httpClient.postForm(method, form, templateDetailType);
    }

    private String resolveSender(String requestSender) {
        String sender = requestSender != null && !requestSender.isBlank() ? requestSender : config.defaultSender();
        if (sender == null) {
            return null;
        }
        return KavenegarValidation.requireSender(sender, "sender");
    }

    private void requireType(Integer type) {
        if (type == null) {
            return;
        }
        if (type < 0 || type > 3) {
            throw new KavenegarValidationException("type must be between 0 and 3");
        }
    }

    private void requireHide(Integer hide) {
        if (hide == null) {
            return;
        }
        if (hide != 0 && hide != 1) {
            throw new KavenegarValidationException("hide must be 0 or 1");
        }
    }

    private Map<String, Object> childQuery(String childApiKey, String childLocalId) {
        Map<String, Object> query = new LinkedHashMap<>();
        putIfPresent(query, "apiKey", childApiKey);
        putIfPresent(query, "localId", childLocalId);
        return query;
    }

    private Map<String, Object> childForm(String childApiKey, String childLocalId) {
        return childQuery(childApiKey, childLocalId);
    }

    private void putIfPresent(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private String writeJson(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JacksonException ex) {
            throw new KavenegarValidationException("Request body is invalid", ex);
        }
    }

    private <T> T first(List<T> rows, String field) {
        if (rows == null || rows.isEmpty()) {
            throw new KavenegarValidationException("empty " + field + " result");
        }
        return rows.getFirst();
    }
}
