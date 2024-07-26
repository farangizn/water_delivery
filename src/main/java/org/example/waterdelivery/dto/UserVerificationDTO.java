package org.example.waterdelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerificationDTO {

    private UUID tgUserId;
    private Float longitude;
    private Float latitude;
    private UUID districtId;
    private String addressLine;
}
