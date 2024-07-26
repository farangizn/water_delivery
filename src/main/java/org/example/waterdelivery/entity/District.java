package org.example.waterdelivery.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterdelivery.entity.abs.AbsEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class District extends AbsEntity {

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;
}
