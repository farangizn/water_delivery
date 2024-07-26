package org.example.waterdelivery.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageReplyMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import org.example.waterdelivery.dto.Location;
import org.example.waterdelivery.entity.*;
import org.example.waterdelivery.entity.enums.TelegramState;
import org.example.waterdelivery.repo.*;
import org.example.waterdelivery.utils.DistanceUtil;
import org.example.waterdelivery.utils.PhoneRepairUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.function.IntFunction;

@Service
@RequiredArgsConstructor
public class BotServiceImpl implements BotService {

    public static Location companyLocation = new Location(
            41.327f,69.22763f
    );

    private final TelegramUserRepository telegramUserRepository;
    private final UserRepository userRepository;
    private final TelegramBot telegramBot;
    private final BotUtils botUtils;
    private final RegionRepository regionRepository;
    private final BottleTypeRepository bottleTypeRepository;
    private final DeliveryTimeRepository deliveryTimeRepository;
    private final OrderRepository orderRepository;
    private final DistanceUtil distanceUtil;

    @Override
    public TelegramUser getOrCreateTelegramUser(Long chatId) {
        Optional<TelegramUser> tgUserOpt = telegramUserRepository.findByChatId(chatId);
        if (tgUserOpt.isPresent()) {
            return tgUserOpt.get();
        } else {
            TelegramUser telegramUser = new TelegramUser(chatId);
            telegramUserRepository.save(telegramUser);
            return telegramUser;
        }
    }

    @Override
    public void acceptStartSendShareContact(Message message, TelegramUser tgUser) {
        if (tgUser.getUser() == null) {
            SendMessage sendMessage = new SendMessage(message.chat().id(), BotConstant.SHARE_CONTACT_MSG);
            sendMessage.replyMarkup(botUtils.generateContactButton());
            telegramBot.execute(sendMessage);
            tgUser.setState(TelegramState.SHARE_CONTACT);
            telegramUserRepository.save(tgUser);
        }
    }

    @Override
    public void acceptContactSendRegionButton(Contact contact, TelegramUser tgUser) {
        String phoneNumber = PhoneRepairUtil.repairPhone(contact.phoneNumber());
        User user = User.builder().phone(phoneNumber).build();
        userRepository.save(user);
        tgUser.setUser(user);
        tgUser.setState(TelegramState.SELECT_REGION);
        telegramBot.execute(new SendMessage(tgUser.getChatId(), BotConstant.SELECT_REGION).replyMarkup(new ReplyKeyboardRemove(true)));
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), BotConstant.REGIONS);
        sendMessage.replyMarkup(botUtils.generateRegionButtons());
        SendResponse response = telegramBot.execute(sendMessage);
        tgUser.setDeletingMessage(response.message().messageId());
        telegramUserRepository.save(tgUser);
    }

    @Override
    public void acceptRegionSendLocation(CallbackQuery callbackQuery, TelegramUser tgUser) {
        telegramBot.execute(new DeleteMessage(tgUser.getChatId(), tgUser.getDeletingMessage()));
        UUID regionId = UUID.fromString(callbackQuery.data());
        Region region = regionRepository.findById(regionId).orElseThrow(() -> new RuntimeException("Region not found"));
        tgUser.setRegion(region);
        sendLocation(tgUser);
    }

    @Override
    public void sendLocation(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), BotConstant.PLEASE_SHARE_LOCATION);
        sendMessage.replyMarkup(botUtils.generateLocationButton());
        telegramBot.execute(sendMessage);
        tgUser.setState(TelegramState.SHARE_LOCATION);
        telegramUserRepository.save(tgUser);
    }

    @Override
    public void sendCabinet(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), "Your cabinet:");
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton(BotConstant.ORDER_BTN)
                        .callbackData(BotConstant.START_ORDERING)
        ));
        telegramBot.execute(sendMessage);
        tgUser.setState(TelegramState.START_ORDERING);
        telegramUserRepository.save(tgUser);
    }

    @Override
    public void startOrdering(TelegramUser tgUser) {
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), BotConstant.SELECT_BOTTLE_TYPE);
        sendMessage.replyMarkup(botUtils.bottleTypeButtons());
        tgUser.setState(TelegramState.SELECT_BOTTLE_TYPE);
        telegramUserRepository.save(tgUser);
        telegramBot.execute(sendMessage);
    }

    @Override
    public void acceptBottleTypeShowNumberSelection(CallbackQuery callbackQuery, TelegramUser tgUser) {
        Integer bottleTypeId = Integer.parseInt(callbackQuery.data());
        BottleType bottleType = bottleTypeRepository.findById(bottleTypeId).orElseThrow(() -> new RuntimeException("Bottle type not found"));
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), BotConstant.PLEASE_CHOOSE_NUMBER);
        sendMessage.replyMarkup(botUtils.generateBottleNumberButtons(tgUser));
        SendResponse response = telegramBot.execute(sendMessage);
        tgUser.setState(TelegramState.SELECT_BOTTLE_NUMBER);
        tgUser.setBottleType(bottleType);
        tgUser.setEditingMessageId(response.message().messageId());
        telegramUserRepository.save(tgUser);

        SendMessage message = new SendMessage(tgUser.getChatId(), BotConstant.CONFIRM_BTN);
        // need to show the total price here
        message.replyMarkup(new InlineKeyboardMarkup(new InlineKeyboardButton(BotConstant.CONFIRM_BTN).callbackData(BotConstant.CONFIRM_BTN)));
        telegramBot.execute(message);
    }

    @Override
    public void changeBottleNumber(CallbackQuery callbackQuery, TelegramUser tgUser) {
        String data = callbackQuery.data();

        if (data.equals(BotConstant.CONFIRM_BTN)) {
            showAvailableDeliveryTimes(tgUser);
        } else if (data.equals(BotConstant.PLUS)) {
            tgUser.setBottleCount(tgUser.getBottleCount() + 2);
        } else if (data.equals(BotConstant.MINUS)) {
            if (tgUser.getBottleCount() > 2) {
                tgUser.setBottleCount(tgUser.getBottleCount() - 2);
            }
        }
        telegramUserRepository.save(tgUser);
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(
                tgUser.getChatId(), tgUser.getEditingMessageId()
        );
        editMessageReplyMarkup.replyMarkup(botUtils.generateBottleNumberButtons(tgUser));
        telegramBot.execute(editMessageReplyMarkup);


    }

    @Override
    public void acceptOrderTimeShowConfirmation(CallbackQuery callbackQuery, TelegramUser tgUser) {
        LocalDate day = extractDayFromCallbackQuery(callbackQuery);
        DeliveryTime deliveryTime = extractDeliveryTimeFromCallbackQuery(callbackQuery);
        tgUser.setCurrentOrderDeliveryTIme(deliveryTime);
        tgUser.setCurrentOrderDay(day);
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), generateOrderText(tgUser));
        sendMessage.parseMode(ParseMode.HTML);
        sendMessage.replyMarkup(new InlineKeyboardMarkup(
                new InlineKeyboardButton(
                        BotConstant.CONFIRM_BTN
                ).callbackData(BotConstant.CONFIRM_ORDER),
                new InlineKeyboardButton(
                        BotConstant.CANCEL
                ).callbackData(BotConstant.CANCEL)
        ));
        tgUser.setState(TelegramState.CREATE_ORDER);
        telegramUserRepository.save(tgUser);
        telegramBot.execute(sendMessage);
    }

    private String generateOrderText(TelegramUser tgUser) {
        return BotConstant.ORDER_INFO.formatted(
                tgUser.getBottleType().getType(),
                tgUser.getBottleCount(),
                tgUser.calcTotalAmountOfCurrentOrder(),
                tgUser.getCurrentOrderDay(),
                tgUser.getCurrentOrderDeliveryTIme()
        );
    }

    public void makeAnOrder(CallbackQuery callbackQuery, TelegramUser tgUser) {
        if (callbackQuery.data().equals(BotConstant.CANCEL)) {
            sendCabinet(tgUser);
            return;
        }
        Order order = Order.builder()
                .id(tgUser.generateOrderId())
                .bottleCount(tgUser.getBottleCount())
                .bottleType(tgUser.getBottleType())
                .day(tgUser.getCurrentOrderDay())
                .deliveryTime(tgUser.getCurrentOrderDeliveryTIme())
                .location(tgUser.getLocation())
                .telegramUser(tgUser)
                .build();
        orderRepository.save(order);
        SendMessage sendMessage = new SendMessage(tgUser.getChatId(), BotConstant.ORDER_FINISH_MSG);
        telegramBot.execute(sendMessage);
    }

    private DeliveryTime extractDeliveryTimeFromCallbackQuery(CallbackQuery callbackQuery) {
        int deliveryTimeId = Integer.parseInt(callbackQuery.data().split("/")[1]);
        return deliveryTimeRepository.findById(deliveryTimeId).orElseThrow(() -> new RuntimeException("Delivery time not found"));
    }

    private LocalDate extractDayFromCallbackQuery(CallbackQuery callbackQuery) {
        String str = callbackQuery.data().split("/")[0];
        return LocalDate.parse(str);
    }

    private void showAvailableDeliveryTimes(TelegramUser tgUser) {
        List<DeliveryTime> deliveryTimes = deliveryTimeRepository.findAll();
        showNextThreeDays(tgUser);
//        tomorrow();
//        theDayAfterTomorrow();
    }

    private void showNextThreeDays(TelegramUser tgUser) {
        List<LocalDate> days = generateDays();

        List<DeliveryTime> deliveryTimes = deliveryTimeRepository.findAll();

        for (LocalDate day : days) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            SendMessage sendMessage = new SendMessage(tgUser.getChatId(), getDayAsText(day));
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int i = 0; i < deliveryTimes.size(); i++) {
                if (i == 0 && day.equals(LocalDate.now()) || day.equals(LocalDate.now()) && LocalTime.now().isAfter(deliveryTimes.get(i).getStartTime())) {
                    continue;
                }
                DeliveryTime deliveryTime = deliveryTimes.get(i);
                buttons.add(new InlineKeyboardButton(
                        deliveryTime.toString()
                ).callbackData(day + "/" + deliveryTime.getId().toString()));
            }
            inlineKeyboardMarkup.addRow(buttons.toArray(value -> new InlineKeyboardButton[0]));
            sendMessage.replyMarkup(inlineKeyboardMarkup);
            telegramBot.execute(sendMessage);
        }
        tgUser.setState(TelegramState.CONFIRM_ORDER);
        telegramUserRepository.save(tgUser);
    }

    private String getDayAsText(LocalDate day) {
        if (day.equals(LocalDate.now())) {
            return BotConstant.TODAY;
        } else if (day.equals(LocalDate.now().plusDays(1))) {
            return BotConstant.TOMORROW;
        } else {
            return day.getDayOfWeek().getDisplayName(
                    TextStyle.FULL,
                    Locale.forLanguageTag("uz")
            );
        }
    }

    private List<LocalDate> generateDays() {
        return List.of(
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(2)
        );
    }

    private boolean deliveryTimeIsNow(DeliveryTime deliveryTime) {
        LocalTime now = LocalTime.now();
        return now.isBefore(deliveryTime.getStartTime());
    }

    private boolean canFitToday(DeliveryTime deliveryTime, TelegramUser tgUser) {
        // all orders in current user's district and delivery time according to today
        List<Order> orders = orderRepository.findAllByTelegramUserDistrictAndDeliveryTimeAndDayOrderById(
                tgUser.getDistrict(),
                deliveryTime,
                LocalDate.now()
        );
        // all locations of current user's district and part of the day
        List<Location> locations = orders.stream().map(Order::getLocation).toList();

        String response = distanceUtil.buildDirectionsApiUrl(locations, companyLocation);
        int[] optimizedOrder = distanceUtil.getOptimizedOrder(response);
        System.out.println(Arrays.toString(optimizedOrder));

        long totalTimeInSeconds = distanceUtil.getTotalTime(response);
        long totalTimeInMinutes = totalTimeInSeconds / 60;
        totalTimeInMinutes += 15L * locations.size(); // adding 15 minutes for each location
        return deliveryTime.getStartTime().plusMinutes(totalTimeInMinutes).isBefore(deliveryTime.getEndTime());
    }

    @Override
    public void acceptLocationAndWait(com.pengrad.telegrambot.model.Location newLocation, TelegramUser tgUser) {
        tgUser.setLocation(
                new Location(newLocation.latitude(), newLocation.longitude())
        );
        telegramBot.execute(new SendMessage(tgUser.getChatId(), BotConstant.PLEASE_WAIT).replyMarkup(new ReplyKeyboardRemove(true)));
        tgUser.setState(TelegramState.WAITING);
        telegramUserRepository.save(tgUser);
    }
}
