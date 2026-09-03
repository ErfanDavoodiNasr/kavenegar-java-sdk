package com.ernoxin.kavenegarjavasdk.support;

import lombok.experimental.UtilityClass;

import java.util.Map;

/**
 * Catalog of Kavenegar {@code return.status} codes from official REST documentation.
 */
@UtilityClass
public class KavenegarErrorCatalog {
    private static final Map<Integer, String> MESSAGES = Map.ofEntries(
            Map.entry(200, "درخواست تایید شد"),
            Map.entry(400, "پارامترها ناقص هستند"),
            Map.entry(401, "حساب کاربری غیرفعال شده است"),
            Map.entry(402, "عملیات ناموفق بود"),
            Map.entry(403, "کد شناسائی API-Key معتبر نمی‌باشد"),
            Map.entry(404, "متد نامشخص است"),
            Map.entry(405, "متد Get/Post اشتباه است"),
            Map.entry(406, "پارامترهای اجباری خالی ارسال شده اند"),
            Map.entry(407, "دسترسی به اطلاعات مورد نظر برای شما امکان پذیر نیست"),
            Map.entry(409, "سرور قادر به پاسخگوئی نیست بعدا تلاش کنید"),
            Map.entry(411, "دریافت کننده نامعتبر است"),
            Map.entry(412, "ارسال کننده نامعتبر است"),
            Map.entry(413, "پیام خالی است و یا طول پیام بیش از حد مجاز می‌باشد"),
            Map.entry(414, "حجم درخواست بیشتر از حد مجاز است"),
            Map.entry(415, "اندیس شروع بزرگ تر از کل تعداد شماره های مورد نظر است"),
            Map.entry(416, "IP سرویس مبدا با تنظیمات مطابقت ندارد"),
            Map.entry(417, "تاریخ ارسال اشتباه است و فرمت آن صحیح نمی باشد"),
            Map.entry(418, "اعتبار شما کافی نمی‌باشد"),
            Map.entry(419, "طول آرایه متن و گیرنده و فرستنده هم اندازه نیست"),
            Map.entry(420, "استفاده از لینک در متن پیام برای شما محدود شده است"),
            Map.entry(422, "داده ها به دلیل وجود کاراکتر نامناسب قابل پردازش نیست"),
            Map.entry(424, "الگوی مورد نظر پیدا نشد"),
            Map.entry(426, "استفاده از این متد نیازمند سرویس پیشرفته می‌باشد"),
            Map.entry(427, "استفاده از این خط نیازمند ایجاد سطح دسترسی می باشد"),
            Map.entry(428, "ارسال کد از طریق تماس تلفنی امکان پذیر نیست"),
            Map.entry(429, "IP محدود شده است"),
            Map.entry(431, "ساختار کد صحیح نمی‌باشد"),
            Map.entry(432, "پارامتر کد در متن پیام پیدا نشد"),
            Map.entry(451, "فراخوانی بیش از حد در بازه زمانی مشخص IP محدود شده"),
            Map.entry(501, "فقط امکان ارسال پیام تست به شماره صاحب حساب کاربری وجود دارد"),
            Map.entry(604, "تعداد شماره‌های ارسالی بیش از حد مجاز است"),
            Map.entry(607, "نام تگ انتخابی اشتباه است"),
            Map.entry(800, "ارسال رسانه الزامی است"),
            Map.entry(801, "رسانه نامعتبر است یا امکان پردازش آن وجود ندارد"),
            Map.entry(802, "حجم فایل رسانه مجاز نیست"),
            Map.entry(803, "نسبت ابعاد رسانه مجاز نیست"),
            Map.entry(804, "مدت زمان رسانه مجاز نیست"),
            Map.entry(805, "دسترسی لازم برای آپلود رسانه وجود ندارد"),
            Map.entry(806, "محدودیت مجاز آپلود رسانه عبور کرده است"),
            Map.entry(807, "رسانه تکراری است و قبلا آپلود شده است"),
            Map.entry(808, "شناسه رسانه نامعتبر است"),
            Map.entry(809, "رسانه یافت نشد"),
            Map.entry(810, "رسانه هنوز تایید نشده و در انتظار بررسی است"),
            Map.entry(811, "رسانه توسط سیستم یا اپراتور رد شده است"),
            Map.entry(812, "در پالیسی انتخاب شده امکان استفاده از رسانه وجود ندارد"),
            Map.entry(813, "در خطوط غیر پیام رسان داخلی امکان ارسال رسانه وجود ندارد")
    );

    /**
     * Returns catalog message for a status code, or {@code null} when unknown.
     *
     * @param status Kavenegar return status
     * @return catalog message, or {@code null}
     */
    public static String messageFor(Integer status) {
        if (status == null) {
            return null;
        }
        return MESSAGES.get(status);
    }
}
