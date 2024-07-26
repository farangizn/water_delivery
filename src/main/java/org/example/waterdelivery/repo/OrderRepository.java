package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.DeliveryTime;
import org.example.waterdelivery.entity.District;
import org.example.waterdelivery.entity.Order;
import org.example.waterdelivery.entity.TelegramUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findAllByTelegramUserDistrictAndDeliveryTimeAndDayOrderById(District district, DeliveryTime deliveryTime, LocalDate day);
}