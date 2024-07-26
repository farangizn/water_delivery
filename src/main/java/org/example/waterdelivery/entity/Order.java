package org.example.waterdelivery.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterdelivery.dto.Location;
import org.example.waterdelivery.entity.enums.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private TelegramUser telegramUser;

    private LocalDate day;  // chislo

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @ManyToOne
    private DeliveryTime deliveryTime; // chisloni qaysi vaqtiga

    @CreationTimestamp
    private LocalDateTime createdAt;

    private Integer bottleCount;

    @ManyToOne
    private BottleType bottleType;

    @Embedded
    private Location location;

}
