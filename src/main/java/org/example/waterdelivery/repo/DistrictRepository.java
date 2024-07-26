package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface DistrictRepository extends JpaRepository<District, UUID> {
    List<District> findAllByRegionId(UUID id);

    @Query(value = """
            select * from district where similarity(name, ?1) > 0.2 order by similarity(name, ?1) desc limit 1
            """,
            nativeQuery = true)
    District findSimilarDistrictName(String districtName);
}

