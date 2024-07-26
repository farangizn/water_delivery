package org.example.waterdelivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterdelivery.dto.Location;
import org.example.waterdelivery.entity.abs.AbsEntity;
import org.example.waterdelivery.entity.enums.TelegramState;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class TelegramUser extends AbsEntity {

    private Long chatId;

    @Enumerated(EnumType.STRING)
    private TelegramState state = TelegramState.START;

    @OneToOne(fetch = FetchType.LAZY) // will not perform a heavy join query each time TelegramUser is accessed
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY)
    private District district;

    @Embedded
    private Location location;

    private Integer deletingMessage;

    private Boolean verified = false;

    private String addressLine;

    private Integer bottleCount = 2;

    private Integer editingMessageId;

    private Integer orderCount = 1;

    @ManyToOne
    private BottleType bottleType;

    @ManyToOne
    private DeliveryTime currentOrderDeliveryTIme;

    private LocalDate currentOrderDay;

    public TelegramUser(Long chatId) {
        this.chatId = chatId;
    }

    public Long generateOrderId() {
        return Long.parseLong(chatId + "" + orderCount++);
    }

    public String calcTotalAmountOfCurrentOrder() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.FRANCE);
        return numberFormat.format((long) bottleType.getPrice() *bottleCount);
    }
}
