package com.ernoxin.kavenegarjavasdk.support;

import lombok.experimental.UtilityClass;

/**
 * REST method paths under {@code /v1/{API-KEY}/}. The HTTP client appends {@code .json}.
 */
@UtilityClass
public class KavenegarEndpoints {
    public static final String SMS_SEND = "sms/send";
    public static final String SMS_SEND_ARRAY = "sms/sendarray";
    public static final String SMS_STATUS = "sms/status";
    public static final String SMS_STATUS_LOCAL = "sms/statuslocalmessageid";
    public static final String SMS_STATUS_BY_RECEPTOR = "sms/statusbyreceptor";
    public static final String SMS_SELECT = "sms/select";
    public static final String SMS_SELECT_OUTBOX = "sms/selectoutbox";
    public static final String SMS_LATEST_OUTBOX = "sms/latestoutbox";
    public static final String SMS_COUNT_OUTBOX = "sms/countoutbox";
    public static final String SMS_CANCEL = "sms/cancel";
    public static final String SMS_RECEIVE = "sms/receive";
    public static final String SMS_INBOX_PAGED = "sms/inboxpaged";
    public static final String SMS_COUNT_INBOX = "sms/countinbox";
    public static final String LINE_BLOCKED_LIST = "line/blocked/list";
    public static final String LINE_BLOCKED_ADD = "line/blocked/add";
    public static final String LINE_BLOCKED_EXISTS = "line/blocked/exists";
    public static final String LINE_BLOCKED_REMOVE = "line/blocked/remove";
    public static final String VERIFY_LOOKUP = "verify/lookup";
    public static final String VERIFY_TEMPLATE_LIST = "verify/templatelist";
    public static final String VERIFY_CLONE_TEMPLATE = "verify/clonetemplate";
    public static final String VERIFY_ADD_TEMPLATE = "verify/addtemplate";
    public static final String VERIFY_UPDATE_TEMPLATE = "verify/updatetemplate";
    public static final String VERIFY_GET_TEMPLATE = "verify/gettemplate";
    public static final String VERIFY_DELETE_TEMPLATE = "verify/deletetemplate";
    public static final String CALL_MAKE_TTS = "call/maketts";
    public static final String ACCOUNT_INFO = "account/info";
    public static final String ACCOUNT_CONFIG = "account/config";
    public static final String MEDIA_UPLOAD = "media/upload";
    public static final String MEDIA_LIST = "media/list";
    public static final String MEDIA_GET = "media/get";
    public static final String MEDIA_DELETE = "media/delete";
    public static final String UTILS_GET_DATE = "utils/getdate";
}
