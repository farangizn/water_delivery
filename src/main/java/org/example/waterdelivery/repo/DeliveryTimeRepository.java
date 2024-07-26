package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.DeliveryTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryTimeRepository extends JpaRepository<DeliveryTime, Integer> {
}