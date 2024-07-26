package org.example.waterdelivery.bot;

import com.pengrad.telegrambot.model.*;
import lombok.RequiredArgsConstructor;
import org.example.waterdelivery.entity.TelegramUser;
import org.example.waterdelivery.entity.enums.TelegramState;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    @Async
    public void handle(Update update) {
        if (update.message() != null) {
            Message message = update.message();
            TelegramUser tgUser = botService.getOrCreateTelegramUser(message.chat().id());
            if (message.text() != null) {
                String text = message.text();
                if (text.equals("/start")) {
                    if (tgUser.getVerified()) {
                        botService.sendCabinet(tgUser);
                    } else {
                        botService.acceptStartSendShareContact(message, tgUser);
                    }
                }
            } else if (message.contact() != null) {
                if (tgUser.getState().equals(TelegramState.SHARE_CONTACT)) {
                    Contact contact = message.contact();
                    botService.acceptContactSendRegionButton(contact, tgUser);
                }
            } else if (message.location() != null) {
                if (tgUser.getState().equals(TelegramState.SHARE_LOCATION)) {
                    Location location = message.location();
                    botService.acceptLocationAndWait(location, tgUser);
                }
            }
        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            TelegramUser tgUser = botService.getOrCreateTelegramUser(callbackQuery.from().id());
            if (tgUser.getState().equals(TelegramState.SELECT_REGION)) {
                botService.acceptRegionSendLocation(callbackQuery, tgUser);
            } else if (tgUser.getState().equals(TelegramState.START_ORDERING)) {
                botService.startOrdering(tgUser);
            } else if (tgUser.getState().equals(TelegramState.SELECT_BOTTLE_TYPE)) {
                botService.acceptBottleTypeShowNumberSelection(callbackQuery, tgUser);
            } else if (tgUser.getState().equals(TelegramState.SELECT_BOTTLE_NUMBER)) {
                botService.changeBottleNumber(callbackQuery, tgUser);
            } else if (tgUser.getState().equals(TelegramState.CONFIRM_ORDER)) {
                botService.acceptOrderTimeShowConfirmation(callbackQuery, tgUser);
            } else if (tgUser.getState().equals(TelegramState.CREATE_ORDER)) {
                botService.makeAnOrder(callbackQuery, tgUser);
            }
        }
    }

}
