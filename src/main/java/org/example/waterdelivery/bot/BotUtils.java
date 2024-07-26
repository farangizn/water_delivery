package org.example.waterdelivery.bot;

import com.pengrad.telegrambot.model.request.*;
import lombok.RequiredArgsConstructor;
import org.example.waterdelivery.entity.BottleType;
import org.example.waterdelivery.entity.Region;
import org.example.waterdelivery.entity.TelegramUser;
import org.example.waterdelivery.repo.BottleTypeRepository;
import org.example.waterdelivery.repo.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotUtils {

    private final RegionRepository regionRepository;
    private final BottleTypeRepository bottleTypeRepository;


    public Keyboard generateContactButton() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton(
                        BotConstant.SHARE_CONTACT_BUTTON
                ).requestContact(true)
        ).resizeKeyboard(true);
    }

    public Keyboard generateLocationButton() {
        return new ReplyKeyboardMarkup(
                new KeyboardButton(
                        BotConstant.SHARE_LOCATION_BUTTON
                ).requestLocation(true)
        ).resizeKeyboard(true);
    }

    public Keyboard generateRegionButtons() {
        List<Region> regions = regionRepository.findAll();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        for (Region region : regions) {
            inlineKeyboardMarkup.addRow(
                    new InlineKeyboardButton(region.getName())
                            .callbackData(region.getId().toString())
            );
        }
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup bottleTypeButtons() {
        List<BottleType> bottleTypes = bottleTypeRepository.findAllByActiveTrue();
        InlineKeyboardButton[] buttons = bottleTypes.stream().map(item -> new InlineKeyboardButton(
                item.getType()
        ).callbackData(item.getId().toString())).toArray(InlineKeyboardButton[]::new);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(
                buttons
        );
        return inlineKeyboardMarkup;
    }

    public InlineKeyboardMarkup generateBottleNumberButtons(TelegramUser tgUser) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.addRow(
                new InlineKeyboardButton("+").callbackData(BotConstant.PLUS),
                new InlineKeyboardButton(tgUser.getBottleCount().toString()).callbackData(BotConstant.BOTTLE_COUNT),
                new InlineKeyboardButton("-").callbackData(BotConstant.MINUS)
        );
        return inlineKeyboardMarkup;
    }
}
