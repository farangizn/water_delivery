package org.example.waterdelivery.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterdelivery.entity.abs.AbsEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Region extends AbsEntity {

    private String name;
}
