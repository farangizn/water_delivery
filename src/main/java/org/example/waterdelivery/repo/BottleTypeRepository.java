package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.BottleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BottleTypeRepository extends JpaRepository<BottleType, Integer> {
    List<BottleType> findAllByActiveTrue();
}