package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RegionRepository extends JpaRepository<Region, UUID> {
}