package org.example.waterdelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.waterdelivery.entity.Region;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private Location location;
    private String phone;
    private Region region;
}
