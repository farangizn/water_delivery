package org.example.waterdelivery.bot;

public interface BotConstant {
    String SHARE_CONTACT_MSG = "Iltimos, botdan to'liq foydalanish uchun kontakt yuborish tugmasini bosing";
    String SHARE_CONTACT_BUTTON = "Kontakt yuborish";
    String SHARE_LOCATION_BUTTON = "Lokatsiya yuborish";
    String SELECT_REGION = "Iltimos, viloyat tanlang";
    String REGIONS = "Viloyatlar";
    String PLEASE_SHARE_LOCATION = "Iltimos, lokatsiya yuborish tugmasini bosing";
    String PLEASE_WAIT = "Iltimos, kutib turing. Tez orada operator siz bilan aloqaga chiqadi";
    String ORDER_BTN = "Suv buyurtma qilish";
    String START_ORDERING = "START_ORDERING";
    String SELECT_BOTTLE_TYPE = "Iltimos baklashka turini tanlang";
    String PLEASE_CHOOSE_NUMBER = "Iltimos, baklashka sonini kiriting";
    String PLUS = "PLUS";
    String MINUS = "MINUS";
    String BOTTLE_COUNT = "BOTTLE_NUMBER";
    String CONFIRM_BTN = "Tasdiqlash";
    String TODAY = "Bugun";
    String TOMORROW = "Ertaga";
    String CONFIRM_ORDER = "CONFIRM_ORDER";
    String ORDER_FINISH_MSG = "Buyurtmangiz qabul qilindi. Iltimos, yetkazib berish vaqtida uyda bo'lishni unutmang";
    String ORDER_INFO = """
                <b>Sizning buyurtmangiz:</b>
                <b>%s</b> L dan <b>%d</b> ta
                <b>Jami:</b> %s sum
                Yetkazish vaqti
                Kun: %s
                Vaqt: %s
                """;
    String CANCEL = "Bekor qilish";
}
