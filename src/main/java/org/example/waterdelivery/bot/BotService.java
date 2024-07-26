package org.example.waterdelivery.bot;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Location;
import com.pengrad.telegrambot.model.Message;
import org.example.waterdelivery.entity.TelegramUser;
import org.springframework.stereotype.Service;

@Service
public interface BotService {
    TelegramUser getOrCreateTelegramUser(Long id);

    void acceptStartSendShareContact(Message message, TelegramUser tgUser);

    void acceptContactSendRegionButton(Contact contact, TelegramUser tgUser);

    void acceptRegionSendLocation(CallbackQuery callbackQuery, TelegramUser tgUser);

    void acceptLocationAndWait(Location location, TelegramUser tgUser);
    void sendLocation(TelegramUser telegramUser);

    void sendCabinet(TelegramUser tgUser);

    void startOrdering(TelegramUser tgUser);

    void acceptBottleTypeShowNumberSelection(CallbackQuery callbackQuery, TelegramUser tgUser);

    void changeBottleNumber(CallbackQuery callbackQuery, TelegramUser tgUser);

    void acceptOrderTimeShowConfirmation(CallbackQuery callbackQuery, TelegramUser tgUser);

    void makeAnOrder(CallbackQuery callbackQuery, TelegramUser tgUser);
}
