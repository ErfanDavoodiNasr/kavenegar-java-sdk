<div dir="rtl" align="right">

# <span dir="ltr">SDK</span> جاوا وب‌سرویس کاوه‌نگار

## معرفی

این کتابخانه یک <span dir="ltr">SDK</span> سبک برای [REST API کاوه‌نگار](https://kavenegar.com/rest.html) در پروژه‌های
<span dir="ltr">Spring Boot 3.5.7</span> است. نقطه ورود <span dir="ltr">`KavenegarClient`</span> است.

این پروژه یک کتابخانه است و برنامه اجرایی ندارد. زمان‌ها در API به‌صورت <span dir="ltr">Unix Time</span> هستند.

احراز هویت کاوه‌نگار فقط با قرار دادن کلید در مسیر URL انجام می‌شود
(<span dir="ltr">`https://api.kavenegar.com/v1/{API-KEY}/Scope/Method.json`</span>). جایگزین هدر در مستندات رسمی نیست.
این SDK:

* فقط HTTPS می‌پذیرد
* کلید حساب را در query نمی‌گذارد
* ارسال، Verify و TTS را با <span dir="ltr">POST</span> فرم می‌فرستد تا متن و توکن در query نماند
* کلید را در <span dir="ltr">`toString()`</span> پیکربندی نمایش نمی‌دهد

متدهای حذف‌شده در مستندات فعلی (<span dir="ltr">CountPostalCode</span> و <span dir="ltr">SendByPostalCode</span>) پیاده
نشده‌اند.

فرمت گیرنده طبق مستندات: <span dir="ltr">`09121234567`</span>، <span dir="ltr">`9121234567`</span>،
<span dir="ltr">`+989121234567`</span>، <span dir="ltr">`00989121234567`</span>. برای شماره بین‌المللی در Verify از
<span dir="ltr">`00`</span> + کد کشور استفاده کنید (مثال: <span dir="ltr">`00974211234565`</span>).

فرمت فرستنده: <span dir="ltr">`10004346`</span>، <span dir="ltr">`+9810004346`</span>،
<span dir="ltr">`009810004346`</span>.

---

## پیش‌نیازها

* <span dir="ltr">Java 21</span>
* <span dir="ltr">Spring Boot 3.5.7</span> (برای auto-config؛ استفاده بدون Spring هم ممکن است)

---

## نصب

### مرحله ۱: ساخت و نصب محلی

<div dir="ltr" align="left">

```bash
mvn clean install
```

</div>

### مرحله ۲: افزودن به پروژه مصرف‌کننده

<div dir="ltr" align="left">

```xml

<dependency>
    <groupId>com.ernoxin</groupId>
    <artifactId>kavenegar-java-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

</div>

---

## پیکربندی

پیکربندی <span dir="ltr">fail-fast</span> است. Beanهای Spring فقط با <span dir="ltr">`kavenegar.enabled=true`</span>
ساخته می‌شوند. کلید را از پنل کاوه‌نگار بگیرید.

### کلیدهای <span dir="ltr">application.properties</span>

| کلید                                                  |            الزامی | پیش‌فرض                                            | توضیح                                                            |
|-------------------------------------------------------|------------------:|----------------------------------------------------|------------------------------------------------------------------|
| <span dir="ltr">`kavenegar.enabled`</span>            | بله (auto-config) | <span dir="ltr">`false`</span>                     | برای ساخت bean باید <span dir="ltr">`true`</span> باشد           |
| <span dir="ltr">`kavenegar.api-key`</span>            |               بله | -                                                  | کلید خصوصی پنل (مسیر URL)                                        |
| <span dir="ltr">`kavenegar.default-sender`</span>     |               خیر | -                                                  | خط پیش‌فرض اگر <span dir="ltr">`send`</span> فرستنده نداشته باشد |
| <span dir="ltr">`kavenegar.base-url`</span>           |               خیر | <span dir="ltr">`https://api.kavenegar.com`</span> | فقط HTTPS                                                        |
| <span dir="ltr">`kavenegar.timeout.connect`</span>    |               خیر | <span dir="ltr">`10s`</span>                       | مهلت اتصال                                                       |
| <span dir="ltr">`kavenegar.timeout.read`</span>       |               خیر | <span dir="ltr">`30s`</span>                       | مهلت خواندن پاسخ                                                 |
| <span dir="ltr">`kavenegar.retry.enabled`</span>      |               خیر | <span dir="ltr">`false`</span>                     | retry فقط برای GET خواندنی                                       |
| <span dir="ltr">`kavenegar.retry.max-attempts`</span> |               خیر | <span dir="ltr">`1`</span>                         | تعداد کل تلاش‌ها                                                 |
| <span dir="ltr">`kavenegar.retry.backoff`</span>      |               خیر | <span dir="ltr">`0ms`</span>                       | وقفه بین تلاش‌ها                                                 |
| <span dir="ltr">`kavenegar.http.user-agent`</span>    |               خیر | <span dir="ltr">`KavenegarJavaSdk`</span>          | مقدار User-Agent                                                 |
| <span dir="ltr">`kavenegar.max-recipients`</span>     |               خیر | <span dir="ltr">`200`</span>                       | سقف گیرنده در هر ارسال (سقف API)                                 |
| <span dir="ltr">`kavenegar.max-status-ids`</span>     |               خیر | <span dir="ltr">`500`</span>                       | سقف شناسه در status / select / cancel                            |
| <span dir="ltr">`kavenegar.max-message-length`</span> |               خیر | <span dir="ltr">`4000`</span>                      | سقف طول متن (خطوط پیام‌رسان داخلی)                               |

### <span dir="ltr">timeout</span> و <span dir="ltr">retry</span>

* retry فقط روی خطای شبکه است، نه روی <span dir="ltr">`return.status`</span>.
* این متدها **هرگز** retry نمی‌شوند: <span dir="ltr">`send`</span>، <span dir="ltr">`sendArray`</span>،
  <span dir="ltr">`cancel`</span>، <span dir="ltr">`verifyLookup`</span>، <span dir="ltr">`callTts`</span>،
  <span dir="ltr">`addBlocked`</span>، <span dir="ltr">`removeBlocked`</span>،
  <span dir="ltr">`cloneTemplate`</span>، <span dir="ltr">`addTemplate`</span>،
  <span dir="ltr">`updateTemplate`</span>، <span dir="ltr">`deleteTemplate`</span>،
  <span dir="ltr">`updateAccountConfig`</span>، <span dir="ltr">`uploadMedia`</span>،
  <span dir="ltr">`deleteMedia`</span>، و دریافت صندوق نخوانده
  (<span dir="ltr">`receive(..., true)`</span> و <span dir="ltr">`inboxPaged(..., true, ...)`</span>) چون پیام را خوانده
  می‌کنند.

برای <span dir="ltr">`select`</span>، <span dir="ltr">`selectOutbox`</span> و
<span dir="ltr">`latestOutbox`</span> باید IP سرور را در تنظیمات امنیتی پنل ثبت کنید (کد ۴۰۷).

### نمونه تنظیمات

<div dir="ltr" align="left">

```properties
kavenegar.enabled=true
kavenegar.api-key=YOUR_API_KEY
kavenegar.default-sender=10004346
```

</div>

استفاده بدون Spring:

<div dir="ltr" align="left">

```java
KavenegarConfig config = KavenegarConfig.builder("YOUR_API_KEY")
        .defaultSender("10004346")
        .build();
KavenegarClient client = new KavenegarClient(config);
```

</div>

تزریق در Spring:

<div dir="ltr" align="left">

```java

@Service
@RequiredArgsConstructor
public class SmsService {
    private final KavenegarClient client;
}
```

</div>

موفقیت یعنی HTTP 2xx و <span dir="ltr">`return.status == 200`</span>. وگرنه
<span dir="ltr">`KavenegarApiException`</span>. ورودی نامعتبر قبل از شبکه
<span dir="ltr">`KavenegarValidationException`</span> است. خطای شبکه
<span dir="ltr">`KavenegarTransportException`</span>.

---

## جدول متدها

| متد SDK                                      | API رسمی                                               | HTTP در SDK    | خروجی                                                | توضیح                                                            |
|----------------------------------------------|--------------------------------------------------------|----------------|------------------------------------------------------|------------------------------------------------------------------|
| <span dir="ltr">`send`</span>                | <span dir="ltr">`/v1/{API-KEY}/sms/send.json`</span>   | POST فرم       | <span dir="ltr">`List<MessageResult>`</span>         | یک متن به چند گیرنده (حداکثر ۲۰۰)                                |
| <span dir="ltr">`sendArray`</span>           | <span dir="ltr">`sms/sendarray.json`</span>            | POST فرم       | <span dir="ltr">`List<MessageResult>`</span>         | آرایه‌های JSON هم‌اندازه فرستنده/گیرنده/متن                      |
| <span dir="ltr">`status`</span>              | <span dir="ltr">`sms/status.json`</span>               | GET            | <span dir="ltr">`List<StatusResult>`</span>          | وضعیت تا ۴۸ ساعت؛ حداکثر ۵۰۰ شناسه                               |
| <span dir="ltr">`statusByLocalId`</span>     | <span dir="ltr">`sms/statuslocalmessageid.json`</span> | GET            | <span dir="ltr">`List<LocalStatusResult>`</span>     | وضعیت با localid (حدود ۱۲ ساعت)                                  |
| <span dir="ltr">`statusByReceptor`</span>    | <span dir="ltr">`sms/statusbyreceptor.json`</span>     | GET            | <span dir="ltr">`List<ReceptorStatusResult>`</span>  | وضعیت‌های یک شماره؛ بازه حداکثر ۱ روز                            |
| <span dir="ltr">`select`</span>              | <span dir="ltr">`sms/select.json`</span>               | GET            | <span dir="ltr">`List<MessageResult>`</span>         | جزئیات پیام؛ نیاز به IP مجاز                                     |
| <span dir="ltr">`selectOutbox`</span>        | <span dir="ltr">`sms/selectoutbox.json`</span>         | GET            | <span dir="ltr">`List<MessageResult>`</span>         | لیست ارسال بازه؛ حداکثر ۱ روز؛ شروع حداکثر ۴ روز قبل؛ IP مجاز    |
| <span dir="ltr">`latestOutbox`</span>        | <span dir="ltr">`sms/latestoutbox.json`</span>         | GET            | <span dir="ltr">`List<MessageResult>`</span>         | آخرین ارسال‌ها؛ pagesize حداکثر ۲۰۰؛ IP مجاز                     |
| <span dir="ltr">`countOutbox`</span>         | <span dir="ltr">`sms/countoutbox.json`</span>          | GET            | <span dir="ltr">`CountOutboxResult`</span>           | تعداد/هزینه ارسال در بازه (حداکثر ۱ روز)                         |
| <span dir="ltr">`cancel`</span>              | <span dir="ltr">`sms/cancel.json`</span>               | POST فرم       | <span dir="ltr">`List<StatusResult>`</span>          | لغو ارسال زمان‌بندی‌شده؛ حداکثر ۵۰۰ شناسه                        |
| <span dir="ltr">`receive`</span>             | <span dir="ltr">`sms/receive.json`</span>              | GET            | <span dir="ltr">`List<InboxMessage>`</span>          | صندوق؛ حداکثر ۱۰۰ ردیف؛ نخوانده پیام را خوانده می‌کند            |
| <span dir="ltr">`inboxPaged`</span>          | <span dir="ltr">`sms/inboxpaged.json`</span>           | GET            | <span dir="ltr">`PagedResult<InboxMessage>`</span>   | صندوق صفحه‌بندی؛ حداکثر ۲۰۰ در صفحه؛ بازه حداکثر ۲ روز           |
| <span dir="ltr">`countInbox`</span>          | <span dir="ltr">`sms/countinbox.json`</span>           | GET            | <span dir="ltr">`CountInboxResult`</span>            | تعداد دریافتی؛ بازه حداکثر ۱ روز                                 |
| <span dir="ltr">`listBlocked`</span>         | <span dir="ltr">`line/blocked/list.json`</span>        | GET            | <span dir="ltr">`PagedResult<BlockedNumber>`</span>  | شماره‌هایی که خط را مسدود کرده‌اند                               |
| <span dir="ltr">`addBlocked`</span>          | <span dir="ltr">`line/blocked/add.json`</span>         | POST فرم       | <span dir="ltr">`List<BlockedMutationResult>`</span> | افزودن به لیست سیاه خط (حداکثر ۲۰۰)                              |
| <span dir="ltr">`blockedExists`</span>       | <span dir="ltr">`line/blocked/exists.json`</span>      | GET            | <span dir="ltr">`List<BlockedMutationResult>`</span> | بررسی حضور در لیست سیاه (حداکثر ۲۰۰)                             |
| <span dir="ltr">`removeBlocked`</span>       | <span dir="ltr">`line/blocked/remove.json`</span>      | DELETE         | <span dir="ltr">`BlockedRemoveResult`</span>         | حذف از لیست سیاه                                                 |
| <span dir="ltr">`verifyLookup`</span>        | <span dir="ltr">`verify/lookup.json`</span>            | POST فرم       | <span dir="ltr">`MessageResult`</span>               | OTP / الگوی خدماتی؛ اولویت بالا                                  |
| <span dir="ltr">`listTemplates`</span>       | <span dir="ltr">`verify/templatelist.json`</span>      | GET            | <span dir="ltr">`PagedResult<VerifyTemplate>`</span> | لیست الگوها                                                      |
| <span dir="ltr">`cloneTemplate`</span>       | <span dir="ltr">`verify/clonetemplate.json`</span>     | POST فرم       | <span dir="ltr">`CloneTemplateResult`</span>         | کپی الگوی تأییدشده                                               |
| <span dir="ltr">`addTemplate`</span>         | <span dir="ltr">`verify/addtemplate.json`</span>       | POST فرم       | <span dir="ltr">`VerifyTemplateDetail`</span>        | ایجاد الگو                                                       |
| <span dir="ltr">`updateTemplate`</span>      | <span dir="ltr">`verify/updatetemplate.json`</span>    | POST فرم       | <span dir="ltr">`VerifyTemplateDetail`</span>        | ویرایش الگو                                                      |
| <span dir="ltr">`getTemplate`</span>         | <span dir="ltr">`verify/gettemplate.json`</span>       | GET            | <span dir="ltr">`VerifyTemplate`</span>              | یک الگو                                                          |
| <span dir="ltr">`deleteTemplate`</span>      | <span dir="ltr">`verify/deletetemplate.json`</span>    | DELETE         | void                                                 | حذف الگو                                                         |
| <span dir="ltr">`callTts`</span>             | <span dir="ltr">`call/maketts.json`</span>             | POST فرم       | <span dir="ltr">`List<MessageResult>`</span>         | تماس صوتی؛ پارامتر repeat در مستندات غیرفعال است و ارسال نمی‌شود |
| <span dir="ltr">`accountInfo`</span>         | <span dir="ltr">`account/info.json`</span>             | GET            | <span dir="ltr">`AccountInfo`</span>                 | اعتبار باقی‌مانده (ریال) و نوع حساب                              |
| <span dir="ltr">`getAccountConfig`</span>    | <span dir="ltr">`account/config.json`</span>           | GET            | <span dir="ltr">`AccountConfig`</span>               | خواندن تنظیمات                                                   |
| <span dir="ltr">`updateAccountConfig`</span> | <span dir="ltr">`account/config.json`</span>           | POST فرم       | <span dir="ltr">`AccountConfig`</span>               | تغییر تنظیمات                                                    |
| <span dir="ltr">`uploadMedia`</span>         | <span dir="ltr">`media/upload.json`</span>             | POST multipart | <span dir="ltr">`MediaFile`</span>                   | آپلود رسانه خطوط پیام‌رسان داخلی                                 |
| <span dir="ltr">`listMedia`</span>           | <span dir="ltr">`media/list.json`</span>               | GET            | <span dir="ltr">`MediaListResult`</span>             | لیست رسانه                                                       |
| <span dir="ltr">`getMedia`</span>            | <span dir="ltr">`media/get.json`</span>                | GET            | <span dir="ltr">`MediaFile`</span>                   | یک رسانه                                                         |
| <span dir="ltr">`deleteMedia`</span>         | <span dir="ltr">`media/delete.json`</span>             | DELETE         | <span dir="ltr">`MediaDeleteResult`</span>           | حذف رسانه                                                        |
| <span dir="ltr">`getServerDate`</span>       | <span dir="ltr">`/v1/0/utils/getdate.json`</span>      | GET            | <span dir="ltr">`ServerDate`</span>                  | ساعت سرور؛ بدون کلید حساب شما                                    |

---

## ارسال ساده — <span dir="ltr">`send`</span>

یک متن برای چند گیرنده. اگر <span dir="ltr">`sender`</span> خالی باشد از
<span dir="ltr">`kavenegar.default-sender`</span> یا خط پیش‌فرض حساب استفاده می‌شود.
<span dir="ltr">`date`</span> خالی یعنی ارسال فوری؛ در غیر این صورت Unix آینده.

<div dir="ltr" align="left">

```java
List<MessageResult> sent = client.send(new SendRequest(
        List.of("09121234567", "09361234567"),
        "خدمات پیام کوتاه کاوه نگار",
        "10004346",
        null,
        null,
        null,
        null,
        null,
        null,
        null
));
Long messageId = sent.getFirst().messageId();
```

</div>

زمان‌بندی و شناسه محلی (جلوگیری از ارسال تکراری؛ تعداد باید برابر گیرنده‌ها باشد):

<div dir="ltr" align="left">

```java
client.send(new SendRequest(
        List.of("09121234567"),
        "یادآوری",
                "10004346",
                Instant.now().plus(2, ChronoUnit.HOURS).getEpochSecond(),
        1,
                List.of("order-1001"),
        0,
                "campaign-a",
                null,
                null
                ));
```

</div>

### فیلدهای <span dir="ltr">SendRequest</span>

| فیلد                               | نوع                                       | الزامی | توضیح                                                          |
|------------------------------------|-------------------------------------------|-------:|----------------------------------------------------------------|
| <span dir="ltr">`receptors`</span> | <span dir="ltr">List&lt;String&gt;</span> |    بله | حداکثر ۲۰۰ شماره                                               |
| <span dir="ltr">`message`</span>   | <span dir="ltr">String</span>             |    بله | متن؛ داخلی تا ۴۰۰۰، سایر خطوط تا ۱۸۰۰                          |
| <span dir="ltr">`sender`</span>    | <span dir="ltr">String</span>             |    خیر | خط فرستنده                                                     |
| <span dir="ltr">`date`</span>      | <span dir="ltr">Long</span>               |    خیر | Unix ارسال؛ گذشته رد می‌شود                                    |
| <span dir="ltr">`type`</span>      | <span dir="ltr">Integer</span>            |    خیر | ۰ تا ۳؛ فقط خطوط ۳۰۰۰؛ پیش‌فرض API برابر ۱                     |
| <span dir="ltr">`localIds`</span>  | <span dir="ltr">List&lt;String&gt;</span> |    خیر | هم‌اندازه گیرنده‌ها                                            |
| <span dir="ltr">`hide`</span>      | <span dir="ltr">Integer</span>            |    خیر | ۱ = مخفی کردن گیرنده در پنل                                    |
| <span dir="ltr">`tag`</span>       | <span dir="ltr">String</span>             |    خیر | تگ از پیش ساخته در پنل؛ حداکثر ۲۰۰؛ حروف/عدد انگلیسی و `-` `_` |
| <span dir="ltr">`policy`</span>    | <span dir="ltr">String</span>             |    خیر | نام جریان ارسال حساب                                           |
| <span dir="ltr">`mediaId`</span>   | <span dir="ltr">String</span>             |    خیر | UUID رسانه؛ فقط خطوط پیام‌رسان داخلی                           |

خروجی هر ردیف: <span dir="ltr">`messageId`</span>، <span dir="ltr">`message`</span>،
<span dir="ltr">`status`</span>، <span dir="ltr">`statusText`</span>، <span dir="ltr">`sender`</span>،
<span dir="ltr">`receptor`</span>، <span dir="ltr">`date`</span>، <span dir="ltr">`cost`</span> (ریال).

---

## ارسال آرایه‌ای — <span dir="ltr">`sendArray`</span>

چند متن مختلف از چند خط. API فقط POST می‌پذیرد. طول سه لیست اجباری باید برابر باشد.

<div dir="ltr" align="left">

```java
List<MessageResult> rows = client.sendArray(new SendArrayRequest(
        List.of("09120000001", "09120000002", "09120000003"),
        List.of("30002626", "30002627", "30002727"),
        List.of("وب سرویس ارسال", "وب سرویس پیامک", "کاوه نگار"),
        null,
        null,
        null,
        null,
        null,
        null,
        null
));
```

</div>

### فیلدهای <span dir="ltr">SendArrayRequest</span>

| فیلد                                     | نوع                                        | الزامی | توضیح                                  |
|------------------------------------------|--------------------------------------------|-------:|----------------------------------------|
| <span dir="ltr">`receptors`</span>       | <span dir="ltr">List&lt;String&gt;</span>  |    بله | گیرنده‌ها                              |
| <span dir="ltr">`senders`</span>         | <span dir="ltr">List&lt;String&gt;</span>  |    بله | خطوط؛ هم‌اندازه گیرنده‌ها              |
| <span dir="ltr">`messages`</span>        | <span dir="ltr">List&lt;String&gt;</span>  |    بله | متن‌ها؛ هم‌اندازه گیرنده‌ها            |
| <span dir="ltr">`date`</span>            | <span dir="ltr">Long</span>                |    خیر | Unix ارسال                             |
| <span dir="ltr">`types`</span>           | <span dir="ltr">List&lt;Integer&gt;</span> |    خیر | اگر باشد باید هم‌اندازه گیرنده‌ها باشد |
| <span dir="ltr">`localMessageIds`</span> | <span dir="ltr">List&lt;String&gt;</span>  |    خیر | جلوگیری از ارسال تکراری                |
| <span dir="ltr">`hide`</span>            | <span dir="ltr">Integer</span>             |    خیر | مثل send                               |
| <span dir="ltr">`tag`</span>             | <span dir="ltr">String</span>              |    خیر | مثل send                               |
| <span dir="ltr">`policy`</span>          | <span dir="ltr">String</span>              |    خیر | مثل send                               |
| <span dir="ltr">`mediaId`</span>         | <span dir="ltr">String</span>              |    خیر | مثل send                               |

اگر طول آرایه‌ها برابر نباشد API کد ۴۱۹ می‌دهد.

---

## وضعیت — <span dir="ltr">`status`</span>

وضعیت دلیوری با <span dir="ltr">`messageid`</span> خروجی ارسال. حداکثر ۵۰۰ شناسه. فقط حدود **۴۸ ساعت** گذشته.
شناسه نامعتبر/آرشیو/غیرمتعلق: <span dir="ltr">`status = 100`</span> (موفقیت HTTP است، نه exception).

<div dir="ltr" align="left">

```java
List<StatusResult> statuses = client.status(List.of(85463238L, 85463239L));
```

</div>

خروجی: <span dir="ltr">`messageId`</span>، <span dir="ltr">`status`</span>، <span dir="ltr">`statusText`</span>.

برای Callback وضعیت می‌توانید URL را در تنظیمات خطوط پنل فعال کنید؛ SDK خودش callback سرور را دریافت نمی‌کند.

---

## وضعیت با شناسه محلی — <span dir="ltr">`statusByLocalId`</span>

اگر <span dir="ltr">`messageid`</span> را ذخیره نکرده‌اید، همان <span dir="ltr">`localid`</span> زمان ارسال را بفرستید.
فقط پیام‌های حدود **۱۲ ساعت** گذشته. اگر آن localid در ارسال‌ها نبود، <span dir="ltr">`status = 100`</span>.

<div dir="ltr" align="left">

```java
List<LocalStatusResult> rows = client.statusByLocalId(List.of("450", "order-1001"));
```

</div>

خروجی: <span dir="ltr">`messageId`</span>، <span dir="ltr">`localId`</span>، <span dir="ltr">`status`</span>،
<span dir="ltr">`statusText`</span>.

---

## وضعیت با شماره گیرنده — <span dir="ltr">`statusByReceptor`</span>

لیست وضعیت پیام‌هایی که به یک شماره رفته‌اند. <span dir="ltr">`startdate`</span> اجباری است.
بازه حداکثر **۱ روز**. اگر <span dir="ltr">`enddate`</span> خالی باشد API یک روز در نظر می‌گیرد.

<div dir="ltr" align="left">

```java
List<ReceptorStatusResult> rows = client.statusByReceptor("09121234567", 1735677000L, null);
```

</div>

خروجی: <span dir="ltr">`messageId`</span>، <span dir="ltr">`receptor`</span>، <span dir="ltr">`status`</span>،
<span dir="ltr">`statusText`</span>. برای متن و تاریخ پیام از <span dir="ltr">`select`</span> استفاده کنید.

---

## جزئیات پیام — <span dir="ltr">`select`</span>

مثل status ولی با متن، فرستنده، گیرنده، تاریخ و هزینه. حداکثر ۵۰۰ شناسه. **نیاز به IP مجاز**.
برای کنترل وضعیت بهتر است <span dir="ltr">`status`</span> را بزنید (حجم کمتر).

<div dir="ltr" align="left">

```java
List<MessageResult> details = client.select(List.of(30034577L, 30034578L));
```

</div>

---

## لیست ارسال بازه — <span dir="ltr">`selectOutbox`</span>

فهرست پیامک‌های ارسالی. بازه حداکثر **۱ روز**. تاریخ شروع نباید کوچک‌تر از **۴ روز قبل** باشد.
**نیاز به IP مجاز**. <span dir="ltr">`endDate`</span> و <span dir="ltr">`sender`</span> اختیاری‌اند.

<div dir="ltr" align="left">

```java
List<MessageResult> outbox = client.selectOutbox(1409533200L, 1410570000L, "10004346");
```

</div>

---

## آخرین ارسال‌ها — <span dir="ltr">`latestOutbox`</span>

**نیاز به IP مجاز**. <span dir="ltr">`pagesize`</span> حداکثر ۲۰۰ (در مستندات جدید؛ قبلاً ۵۰۰ بود).
هر دو آرگومان می‌توانند <span dir="ltr">`null`</span> باشند.

<div dir="ltr" align="left">

```java
List<MessageResult> latest = client.latestOutbox(200, null);
List<MessageResult> latestOnLine = client.latestOutbox(50, "10004346");
```

</div>

---

## شمارش ارسال — <span dir="ltr">`countOutbox`</span>

بازه حداکثر **۱ روز**. شروع حداکثر تا **۳ روز قبل**. <span dir="ltr">`endDate`</span> خالی یعنی تا همین الان.
<span dir="ltr">`status = 1`</span> یعنی تعداد در صف.

<div dir="ltr" align="left">

```java
CountOutboxResult count = client.countOutbox(1409533200L, 1410570000L, 10);
Long parts = count.sumPart();
Long messages = count.sumCount();
Long rials = count.cost();
```

</div>

خروجی: <span dir="ltr">`startDate`</span>، <span dir="ltr">`endDate`</span>، <span dir="ltr">`sumPart`</span>
(تعداد صفحات پیامک)، <span dir="ltr">`sumCount`</span>، <span dir="ltr">`cost`</span>.

---

## لغو زمان‌بندی — <span dir="ltr">`cancel`</span>

پیامک‌هایی که با <span dir="ltr">`date`</span> زمان‌بندی شده‌اند. حداکثر ۵۰۰ شناسه. retry نمی‌شود.

<div dir="ltr" align="left">

```java
List<StatusResult> cancelled = client.cancel(List.of(31031212L, 31031213L));
```

</div>

وضعیت جدید معمولاً ۱۳ (لغو شده / برگشت هزینه) است.

---

## دریافت صندوق — <span dir="ltr">`receive`</span>

<span dir="ltr">`unread = true`</span> معادل <span dir="ltr">`isread=0`</span> است و پیام را **خوانده** می‌کند
(حداکثر ۱۰۰ در هر فراخوانی؛ تا وقتی تعداد خروجی ۱۰۰ است دوباره بزنید). این حالت retry نمی‌شود.

<span dir="ltr">`unread = false`</span> معادل خوانده‌شده است و retry مجاز است.

علاوه بر این متد، در پنل می‌توانید Receive Callback URL بگذارید.

<div dir="ltr" align="left">

```java
List<InboxMessage> unread = client.receive("3000202030", true);
List<InboxMessage> alreadyRead = client.receive("3000202030", false);
```

</div>

خروجی: <span dir="ltr">`messageId`</span>، <span dir="ltr">`message`</span>، <span dir="ltr">`sender`</span>
(فرستنده)، <span dir="ltr">`receptor`</span> (خط شما)، <span dir="ltr">`date`</span>.

---

## صندوق صفحه‌بندی — <span dir="ltr">`inboxPaged`</span>

حداکثر ۲۰۰ پیام در صفحه. بازه <span dir="ltr">`startDate`/`endDate`</span> حداکثر **۲ روز**.
اگر هر دو خالی باشند API دو روز گذشته را می‌گیرد. نخوانده مثل <span dir="ltr">`receive`</span> پیام را خوانده می‌کند.

<div dir="ltr" align="left">

```java
PagedResult<InboxMessage> page = client.inboxPaged("3000202030", false, null, null, 1);
Integer totalPages = page.metadata().totalPages();
```

</div>

<span dir="ltr">`metadata`</span>: <span dir="ltr">`totalCount`</span>، <span dir="ltr">`currentPage`</span>،
<span dir="ltr">`totalPages`</span>، <span dir="ltr">`pageSize`</span>. برای نخوانده،
<span dir="ltr">`totalCount`</span> تعداد باقی‌مانده **خارج از** همین صفحه است.

---

## شمارش دریافتی — <span dir="ltr">`countInbox`</span>

بازه حداکثر **۱ روز**. شروع حداکثر تا **۶۰ روز قبل**. اگر <span dir="ltr">`lineNumber`</span> خالی باشد تعداد همه خطوط
حساب است.
<span dir="ltr">`unread = true`</span> یعنی فقط نخوانده.

<div dir="ltr" align="left">

```java
CountInboxResult inbox = client.countInbox(1409533200L, 1410570000L, "10008284", false);
CountInboxResult allLinesUnread = client.countInbox(1409533200L, null, null, true);
```

</div>

خروجی: <span dir="ltr">`startDate`</span>، <span dir="ltr">`endDate`</span>، <span dir="ltr">`sumCount`</span>.

---

## لیست مسدودی خط — <span dir="ltr">`listBlocked`</span>

شماره‌هایی که این خط را مسدود کرده‌اند. حداکثر ۲۰۰ در صفحه.
اگر <span dir="ltr">`startDate`</span> خالی باشد همان روز در نظر گرفته می‌شود.
اگر <span dir="ltr">`blockReason`</span> خالی باشد فیلتر نمی‌شود.

| <span dir="ltr">blockReason</span> | معنی       |
|-----------------------------------:|------------|
|                                  ۰ | وب‌سرویس   |
|                                  ۱ | پنل کاربری |
|                                  ۲ | لغو ۱۱     |
|                                  ۳ | ادمین      |
|                                 ۱۰ | نامشخص     |

<div dir="ltr" align="left">

```java
PagedResult<BlockedNumber> blocked = client.listBlocked("10002263", 0, 1740300377L, 1);
```

</div>

خروجی ردیف: <span dir="ltr">`number`</span>، <span dir="ltr">`blockReason`</span>، <span dir="ltr">`date`</span>.

---

## افزودن به لیست سیاه — <span dir="ltr">`addBlocked`</span>

حداکثر ۲۰۰ شماره در هر فراخوانی. retry نمی‌شود.

<div dir="ltr" align="left">

```java
List<BlockedMutationResult> added = client.addBlocked(
        "123456",
        List.of("09224443333", "09111235555")
);
```

</div>

<span dir="ltr">`status`</span>: <span dir="ltr">`Active`</span> (اضافه شد) یا
<span dir="ltr">`AlreadyExists`</span> (از قبل بود).

---

## بررسی مسدودی — <span dir="ltr">`blockedExists`</span>

<div dir="ltr" align="left">

```java
List<BlockedMutationResult> exists = client.blockedExists(
        "123456",
        List.of("0911222333", "09224445555")
);
```

</div>

<span dir="ltr">`status`</span>: <span dir="ltr">`Active`</span> (در لیست سیاه) یا
<span dir="ltr">`NotExists`</span>.

---

## حذف از لیست سیاه — <span dir="ltr">`removeBlocked`</span>

HTTP DELETE. retry نمی‌شود.

<div dir="ltr" align="left">

```java
BlockedRemoveResult removed = client.removeBlocked("10002263", List.of("09121234567"));
String message = removed.message();
```

</div>

---

## اعتبارسنجی / OTP — <span dir="ltr">`verifyLookup`</span>

اولویت بالا؛ معمولاً فیلتر تبلیغاتی ندارد و به کشورهای دیگر هم می‌رود. نیاز به سرویس پیشرفته و الگوی تأییدشده در پنل.
فرستنده لازم نیست؛ سیستم خط مناسب را انتخاب می‌کند.

* <span dir="ltr">`token` / `token2` / `token3`</span>: بدون فاصله، حداکثر ۱۰۰ کاراکتر
* <span dir="ltr">`token10`</span>: تا ۵ فاصله
* <span dir="ltr">`token20`</span>: تا ۸ فاصله
* <span dir="ltr">`type`</span>: <span dir="ltr">`sms`</span> (پیش‌فرض) یا <span dir="ltr">`call`</span>
* تماس فقط اگر توکن عددی باشد؛ شماره ثابت ایران خودکار به تماس تبدیل می‌شود

الگوی نمونه در پنل:

<div dir="ltr" align="left">

```
ممنون از خرید شما
کد شارژ : %token
سریال : %token2
مدت اعتبار : %token3
```

</div>

<div dir="ltr" align="left">

```java
MessageResult otp = client.verifyLookup(new VerifyLookupRequest(
        "09361234567",
        "852596",
        null,
        null,
        null,
        null,
        "myverification",
        "sms",
        null
));
```

</div>

با توکن‌های بیشتر و تماس:

<div dir="ltr" align="left">

```java
client.verifyLookup(new VerifyLookupRequest(
                            "09361234567",
        "852596",
                            "ABC12",
                            "30day",
                            "hello world",
                            null,
                            "registerverify",
                            "call",
                            null
));
```

</div>

### فیلدهای <span dir="ltr">VerifyLookupRequest</span>

| فیلد                              | نوع                           | الزامی | توضیح                                                         |
|-----------------------------------|-------------------------------|-------:|---------------------------------------------------------------|
| <span dir="ltr">`receptor`</span> | <span dir="ltr">String</span> |    بله | گیرنده؛ بین‌المللی با ۰۹۷… به شکل <span dir="ltr">`00`</span> |
| <span dir="ltr">`token`</span>    | <span dir="ltr">String</span> |    بله | جایگزین <span dir="ltr">`%token`</span>                       |
| <span dir="ltr">`token2`</span>   | <span dir="ltr">String</span> |    خیر | <span dir="ltr">`%token2`</span>                              |
| <span dir="ltr">`token3`</span>   | <span dir="ltr">String</span> |    خیر | <span dir="ltr">`%token3`</span>                              |
| <span dir="ltr">`token10`</span>  | <span dir="ltr">String</span> |    خیر | تا ۵ فاصله                                                    |
| <span dir="ltr">`token20`</span>  | <span dir="ltr">String</span> |    خیر | تا ۸ فاصله                                                    |
| <span dir="ltr">`template`</span> | <span dir="ltr">String</span> |    بله | نام الگوی انگلیسی بدون فاصله و `_`                            |
| <span dir="ltr">`type`</span>     | <span dir="ltr">String</span> |    خیر | <span dir="ltr">`sms`</span> یا <span dir="ltr">`call`</span> |
| <span dir="ltr">`tag`</span>      | <span dir="ltr">String</span> |    خیر | تگ پنل                                                        |

---

## لیست الگوها — <span dir="ltr">`listTemplates`</span>

صفحه پیش‌فرض API برابر ۱ است. برای کاربر زیرمجموعه <span dir="ltr">`childApiKey`</span> یا
<span dir="ltr">`childLocalId`</span> را بفرستید (در درخواست به <span dir="ltr">`apiKey`</span> /
<span dir="ltr">`localId`</span> نگاشت می‌شود). کلید حساب اصلی همچنان فقط در مسیر URL است.

<div dir="ltr" align="left">

```java
PagedResult<VerifyTemplate> templates = client.listTemplates(1, null, null);
```

</div>

خروجی ردیف: <span dir="ltr">`id`</span>، <span dir="ltr">`name`</span>، <span dir="ltr">`smsMessage`</span>،
<span dir="ltr">`callMessage`</span>، خطوط، <span dir="ltr">`sendPriority`</span>
(<span dir="ltr">`SMS`</span> یا <span dir="ltr">`Call`</span>)، <span dir="ltr">`switchTtl`</span>،
<span dir="ltr">`approvalStatus`</span> (`Rejected` / `PendingReview` / `Approved`).

---

## کپی الگو — <span dir="ltr">`cloneTemplate`</span>

منبع باید تأییدشده باشد. یکی از <span dir="ltr">`sourceTemplateId`</span> یا
<span dir="ltr">`sourceTemplateName`</span> لازم است؛ شناسه بر نام اولویت دارد.

<div dir="ltr" align="left">

```java
CloneTemplateResult cloned = client.cloneTemplate(new CloneTemplateRequest(
        12345,
        null,
        "LoginOtpCopy",
        null,
        null
));
```

</div>

خروجی: <span dir="ltr">`id`</span>، <span dir="ltr">`name`</span>.

---

## ایجاد الگو — <span dir="ltr">`addTemplate`</span>

نام انگلیسی بدون فاصله و `_`. متن پیامک در ارسال/fallback پیامکی باید <span dir="ltr">`%token`</span> داشته باشد.
متن صوتی در تماس/fallback صوتی باید <span dir="ltr">`%token`</span> یا <span dir="ltr">`$token`</span> داشته باشد.
توکن‌های مجاز: خالی، ۲، ۳، ۱۰، ۲۰. اگر fallback فعال باشد <span dir="ltr">`switchTtl`</span> بین ۱ تا ۵ است.

| فیلد                                    | مقادیر                                                                        |
|-----------------------------------------|-------------------------------------------------------------------------------|
| <span dir="ltr">`sourceType`</span>     | ۰ وب‌سایت، ۱ اپ                                                               |
| <span dir="ltr">`sendMethod`</span>     | ۱ پیامک، ۲ تماس                                                               |
| <span dir="ltr">`fallBackMethod`</span> | ۰ پیش‌فرض سیستم، ۱ پیامک، ۲ تماس، ۳ غیرفعال (پیش‌فرض SDK اگر null باشد ۳ است) |

<div dir="ltr" align="left">

```java
VerifyTemplateDetail created = client.addTemplate(new VerifyTemplateRequest(
        null,
        0,
        1,
        2,
        "10004346",
        "1000596446",
        2,
        "https://example.com",
        "MyApp",
        "LoginOtp",
        "کد ورود شما %token است",
        "کد ورود شما %token است",
        null,
        null
));
```

</div>

---

## ویرایش الگو — <span dir="ltr">`updateTemplate`</span>

مثل add، با <span dir="ltr">`templateId`</span> اجباری.

<div dir="ltr" align="left">

```java
VerifyTemplateDetail updated = client.updateTemplate(new VerifyTemplateRequest(
        12345,
        0,
        1,
        3,
        null,
        null,
        null,
        null,
        null,
        "LoginOtp",
        "کد ورود شما %token است",
        null,
        null,
        null
));
```

</div>

خروجی add/update: <span dir="ltr">`templateId`</span>، <span dir="ltr">`name`</span>، نوع منبع، روش ارسال،
fallback، خطوط، <span dir="ltr">`switchTtl`</span>، منبع، متن پیامک و صوت.

---

## دریافت یک الگو — <span dir="ltr">`getTemplate`</span>

<div dir="ltr" align="left">

```java
VerifyTemplate template = client.getTemplate(12345, null, null);
```

</div>

---

## حذف الگو — <span dir="ltr">`deleteTemplate`</span>

موفق یعنی <span dir="ltr">`return.status == 200`</span>. retry نمی‌شود.

<div dir="ltr" align="left">

```java
client.deleteTemplate(12345,null,null);
```

</div>

---

## تماس صوتی — <span dir="ltr">`callTts`</span>

حداکثر ۲۰۰ گیرنده. پارامتر <span dir="ltr">`repeat`</span> در مستندات **غیرفعال** است و SDK آن را نمی‌فرستد.
<span dir="ltr">`localIds`</span> اگر باشد باید هم‌اندازه گیرنده‌ها باشد.

<div dir="ltr" align="left">

```java
List<MessageResult> calls = client.callTts(new CallTtsRequest(
        List.of("09121234567"),
        "خدمات پیام کوتاه کاوه نگار",
        null,
        null,
        null
));
```

</div>

### فیلدهای <span dir="ltr">CallTtsRequest</span>

| فیلد                               | نوع                                       | الزامی | توضیح            |
|------------------------------------|-------------------------------------------|-------:|------------------|
| <span dir="ltr">`receptors`</span> | <span dir="ltr">List&lt;String&gt;</span> |    بله | گیرنده‌ها        |
| <span dir="ltr">`message`</span>   | <span dir="ltr">String</span>             |    بله | متن گفتار        |
| <span dir="ltr">`date`</span>      | <span dir="ltr">Long</span>               |    خیر | Unix ارسال       |
| <span dir="ltr">`localIds`</span>  | <span dir="ltr">List&lt;String&gt;</span> |    خیر | جلوگیری از تکرار |
| <span dir="ltr">`tag`</span>       | <span dir="ltr">String</span>             |    خیر | تگ پنل           |

---

## اطلاعات حساب — <span dir="ltr">`accountInfo`</span>

<div dir="ltr" align="left">

```java
AccountInfo info = client.accountInfo();
Long rials = info.remainCredit();
String type = info.type(); // master یا child
Long expire = info.expireDate();
```

</div>

<span dir="ltr">`expireDate`</span> جنبه امنیتی برای مدیریت مشتریان دارد؛ نگران انقضای حساب اصلی نباشید.

---

## خواندن تنظیمات — <span dir="ltr">`getAccountConfig`</span>

<div dir="ltr" align="left">

```java
AccountConfig cfg = client.getAccountConfig();
```

</div>

خروجی: <span dir="ltr">`apiLogs`</span>، <span dir="ltr">`dailyReport`</span>،
<span dir="ltr">`debugMode`</span>، <span dir="ltr">`defaultSender`</span>،
<span dir="ltr">`minCreditAlarm`</span>، <span dir="ltr">`resendFailed`</span>.

---

## تغییر تنظیمات — <span dir="ltr">`updateAccountConfig`</span>

حداقل یک فیلد باید مقدار داشته باشد. فیلدهای null ارسال نمی‌شوند. retry نمی‌شود.

| فیلد                                    | مقادیر                                                                                                             |
|-----------------------------------------|--------------------------------------------------------------------------------------------------------------------|
| <span dir="ltr">`apiLogs`</span>        | <span dir="ltr">`justfaults`</span> (فقط خطا)، <span dir="ltr">`enabled`</span>، <span dir="ltr">`disabled`</span> |
| <span dir="ltr">`debugMode`</span>      | <span dir="ltr">`enabled`</span> (ارسال واقعی انجام نمی‌شود؛ وضعیت لغو)، <span dir="ltr">`disabled`</span>         |
| <span dir="ltr">`defaultSender`</span>  | خط پیش‌فرض وقتی send فرستنده ندارد                                                                                 |
| <span dir="ltr">`minCreditAlarm`</span> | حداقل اعتبار به ریال برای هشدار                                                                                    |
| <span dir="ltr">`resendFailed`</span>   | <span dir="ltr">`enabled`</span> / <span dir="ltr">`disabled`</span> ارسال مجدد خودکار نرسیده‌ها                   |

<div dir="ltr" align="left">

```java
AccountConfig updated = client.updateAccountConfig(
        new AccountConfigUpdate("justfaults", "enabled", "10004535", 50000, "disabled")
);
```

</div>

---

## آپلود رسانه — <span dir="ltr">`uploadMedia`</span>

فقط برای خطوط پیام‌رسان داخلی. فیلد multipart رسمی <span dir="ltr">`File`</span> است. retry نمی‌شود.
فایل بدون استفاده بعد از ۳۰ روز حذف می‌شود.

| نوع   | MIME                                                                    | پسوند            | حجم  | مدت      |
|-------|-------------------------------------------------------------------------|------------------|------|----------|
| تصویر | <span dir="ltr">`image/jpeg`</span>، <span dir="ltr">`image/gif`</span> | jpg / jpeg / gif | ۱۰MB | نامحدود  |
| ویدئو | <span dir="ltr">`video/mp4`</span>                                      | mp4              | ۲۰MB | ۶۰ ثانیه |

<div dir="ltr" align="left">

```java
byte[] bytes = Files.readAllBytes(Path.of("sample.jpg"));
MediaFile media = client.uploadMedia("sample.jpg", "image/jpeg", bytes);
String mediaId = media.id();
```

</div>

خروجی: <span dir="ltr">`id`</span>، <span dir="ltr">`name`</span>، <span dir="ltr">`mimeType`</span>،
<span dir="ltr">`extension`</span>، <span dir="ltr">`size`</span>، <span dir="ltr">`duration`</span>،
<span dir="ltr">`resolution`</span>، <span dir="ltr">`status`</span> (۰ در حال پردازش، ۱ آماده، ۲ خطا)،
<span dir="ltr">`statusDesc`</span>، <span dir="ltr">`review`</span>
(وضعیت بازبینی ۱ تأیید / ۲ رد).

این <span dir="ltr">`id`</span> را می‌توانید در <span dir="ltr">`SendRequest.mediaId`</span> بگذارید.

---

## لیست رسانه — <span dir="ltr">`listMedia`</span>

پیش‌فرض API: صفحه ۱ و ۵۰ آیتم.

<div dir="ltr" align="left">

```java
MediaListResult list = client.listMedia(1, 50);
Long total = list.total();
List<MediaFile> files = list.list();
```

</div>

---

## دریافت رسانه — <span dir="ltr">`getMedia`</span>

<div dir="ltr" align="left">

```java
MediaFile one = client.getMedia("3fa85f64-5717-4562-b3fc-2c963f66afa6");
```

</div>

---

## حذف رسانه — <span dir="ltr">`deleteMedia`</span>

retry نمی‌شود.

<div dir="ltr" align="left">

```java
MediaDeleteResult deleted = client.deleteMedia(media.id());
Boolean ok = deleted.deleted();
```

</div>

---

## ساعت سرور — <span dir="ltr">`getServerDate`</span>

از <span dir="ltr">`/v1/0/utils/getdate.json`</span> می‌آید و کلید حساب شما را در مسیر نمی‌گذارد.

<div dir="ltr" align="left">

```java
ServerDate clock = client.getServerDate();
Long unix = clock.unixTime();
```

</div>

خروجی: <span dir="ltr">`datetime`</span>، <span dir="ltr">`year`</span>، <span dir="ltr">`month`</span>،
<span dir="ltr">`day`</span>، <span dir="ltr">`hour`</span>، <span dir="ltr">`minute`</span>،
<span dir="ltr">`second`</span>، <span dir="ltr">`unixTime`</span>.

---

## مدل‌های خروجی مشترک

فیلدهای اختیاری nullable هستند تا صفر جعلی ساخته نشود.

### <span dir="ltr">MessageResult</span>

خروجی send / sendArray / select / outbox / verify / TTS:
<span dir="ltr">`messageId`</span>، <span dir="ltr">`message`</span>، <span dir="ltr">`status`</span>،
<span dir="ltr">`statusText`</span>، <span dir="ltr">`sender`</span>، <span dir="ltr">`receptor`</span>،
<span dir="ltr">`date`</span>، <span dir="ltr">`cost`</span>

### <span dir="ltr">StatusResult</span>

<span dir="ltr">`messageId`</span>، <span dir="ltr">`status`</span>، <span dir="ltr">`statusText`</span>

### <span dir="ltr">PagedResult&lt;T&gt;</span>

<span dir="ltr">`entries`</span> و <span dir="ltr">`metadata`</span>
(<span dir="ltr">`totalCount`</span>، <span dir="ltr">`currentPage`</span>،
<span dir="ltr">`totalPages`</span>، <span dir="ltr">`pageSize`</span>)

JSON کاوه‌نگار مخلوط lowercase (<span dir="ltr">`messageid`</span>) و camelCase است؛ مدل‌ها هر دو را می‌خوانند.

---

## نوع نمایش پیام (<span dir="ltr">`type`</span> در send)

فقط خطوط ۳۰۰۰.

| کد | معنی                                       |
|---:|--------------------------------------------|
|  ۰ | خبری؛ روی صفحه ظاهر می‌شود و ذخیره نمی‌شود |
|  ۱ | حافظه موبایل (پیش‌فرض API اگر خالی باشد)   |
|  ۲ | حافظه سیم‌کارت                             |
|  ۳ | ذخیره در نرم‌افزار خاص                     |

---

## کدهای <span dir="ltr">`return.status`</span>

اگر کاتالوگ مقداری داشته باشد همان در <span dir="ltr">`KavenegarApiException`</span> می‌آید.

|  کد | معنی                                                     |
|----:|----------------------------------------------------------|
| ۲۰۰ | تأیید شد                                                 |
| ۴۰۰ | پارامترها ناقص هستند                                     |
| ۴۰۱ | حساب کاربری غیرفعال شده است                              |
| ۴۰۲ | عملیات ناموفق بود                                        |
| ۴۰۳ | API-Key معتبر نیست                                       |
| ۴۰۴ | متد نامشخص است                                           |
| ۴۰۵ | Get/Post اشتباه است                                      |
| ۴۰۶ | پارامترهای اجباری خالی‌اند                               |
| ۴۰۷ | دسترسی نیست؛ IP را در تنظیمات امنیتی ثبت کنید            |
| ۴۰۹ | سرور پاسخگو نیست؛ بعداً تلاش کنید                        |
| ۴۱۱ | گیرنده نامعتبر است                                       |
| ۴۱۲ | فرستنده نامعتبر است                                      |
| ۴۱۳ | متن خالی یا طولانی‌تر از حد مجاز                         |
| ۴۱۴ | حجم درخواست بیش از حد (ارسال ۲۰۰ / وضعیت ۵۰۰)            |
| ۴۱۵ | اندیس شروع بزرگ‌تر از کل شماره‌هاست                      |
| ۴۱۶ | IP مبدأ با تنظیمات مطابقت ندارد                          |
| ۴۱۷ | تاریخ ارسال نامعتبر است                                  |
| ۴۱۸ | اعتبار کافی نیست                                         |
| ۴۱۹ | طول آرایه متن و گیرنده و فرستنده برابر نیست              |
| ۴۲۰ | استفاده از لینک در متن محدود شده است                     |
| ۴۲۲ | کاراکتر نامناسب در داده                                  |
| ۴۲۴ | الگو پیدا نشد یا تأیید نشده                              |
| ۴۲۶ | نیاز به سرویس پیشرفته                                    |
| ۴۲۷ | این خط نیاز به سطح دسترسی دارد                           |
| ۴۲۸ | ارسال کد با تماس ممکن نیست (توکن غیرعددی)                |
| ۴۲۹ | IP محدود شده است                                         |
| ۴۳۱ | ساختار کد صحیح نیست (خط جدید، فاصله، زیرخط در توکن ساده) |
| ۴۳۲ | <span dir="ltr">`%token`</span> در متن الگو نیست         |
| ۴۵۱ | فراخوانی بیش از حد؛ IP محدود شده                         |
| ۵۰۱ | در حالت تست فقط به شماره صاحب حساب می‌توان ارسال کرد     |
| ۶۰۴ | تعداد شماره‌های مسدودی بیش از ۲۰۰ است                    |
| ۶۰۷ | نام تگ اشتباه است                                        |
| ۸۰۰ | ارسال رسانه الزامی است                                   |
| ۸۰۱ | رسانه نامعتبر است                                        |
| ۸۰۲ | حجم فایل مجاز نیست                                       |
| ۸۰۳ | نسبت ابعاد مجاز نیست                                     |
| ۸۰۴ | مدت زمان رسانه مجاز نیست                                 |
| ۸۰۵ | دسترسی آپلود نیست                                        |
| ۸۰۶ | سقف آپلود رد شده است                                     |
| ۸۰۷ | رسانه تکراری است                                         |
| ۸۰۸ | شناسه رسانه نامعتبر است                                  |
| ۸۰۹ | رسانه یافت نشد                                           |
| ۸۱۰ | رسانه هنوز تأیید نشده                                    |
| ۸۱۱ | رسانه رد شده است                                         |
| ۸۱۲ | در این پالیسی رسانه مجاز نیست                            |
| ۸۱۳ | روی خط غیر پیام‌رسان داخلی رسانه مجاز نیست               |

---

## وضعیت پیامک (<span dir="ltr">`entries.status`</span>)

| مقدار | معنی                                      |
|------:|-------------------------------------------|
|     ۱ | در صف ارسال                               |
|     ۲ | زمان‌بندی‌شده                             |
|     ۴ | ارسال شده به مخابرات                      |
|     ۵ | ارسال شده به مخابرات (مثل ۴)              |
|     ۶ | خطا در ارسال (`Failed`)                   |
|    ۱۰ | رسیده به گیرنده (`Delivered`)             |
|    ۱۱ | نرسیده به گیرنده (`Undelivered`)          |
|    ۱۳ | لغو شده یا برگشت هزینه                    |
|    ۱۴ | بلاک تبلیغاتی؛ برگشت هزینه                |
|   ۱۰۰ | شناسه نامعتبر / متعلق به شما نیست / آرشیو |

---

## پرسش‌های رایج

* چرا bean ساخته نمی‌شود؟ <span dir="ltr">`kavenegar.enabled=true`</span> و
  <span dir="ltr">`kavenegar.api-key`</span> را بگذارید.
* خطای ۴۰۳؟ کلید مسیر URL اشتباه است.
* خطای ۴۰۷ روی select/outbox؟ IP سرور را در تنظیمات امنیتی پنل ثبت کنید.
* پیامک تکراری؟ <span dir="ltr">`localIds`</span> یکتا بفرستید.
* OTP فیلتر می‌شود؟ از <span dir="ltr">`verifyLookup`</span> با الگوی تأییدشده استفاده کنید نه send عادی.
* صندوق نخوانده را گم کردم؟ <span dir="ltr">`receive(..., true)`</span> مصرفی است؛ برای مرور از
  <span dir="ltr">`unread = false`</span> یا <span dir="ltr">`inboxPaged`</span> خوانده‌شده استفاده کنید.
* تست بدون هزینه؟ <span dir="ltr">`debugMode=enabled`</span> در config حساب، یا حساب تست پنل (کد ۵۰۱ فقط به شماره صاحب
  حساب).

</div>
