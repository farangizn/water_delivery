package org.example.waterdelivery.component;

import lombok.RequiredArgsConstructor;
import org.example.waterdelivery.bot.BotService;
import org.example.waterdelivery.bot.BotServiceImpl;
import org.example.waterdelivery.dto.Location;
import org.example.waterdelivery.entity.*;
import org.example.waterdelivery.entity.enums.OrderStatus;
import org.example.waterdelivery.entity.enums.RoleName;
import org.example.waterdelivery.repo.*;
import org.example.waterdelivery.utils.DistanceUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final BottleTypeRepository bottleTypeRepository;
    private final DeliveryTimeRepository deliveryTimeRepository;
    private final OrderRepository orderRepository;
    private final DistanceUtil distanceUtil;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) throws Exception {

        if (ddl.equals("create")) {

            BottleType bottleType1 = new BottleType(
                    null, "10", true, 10_000
            );

            BottleType bottleType2 = new BottleType(
                    null, "20", true, 20_000
            );

            bottleTypeRepository.saveAll(List.of(bottleType1, bottleType2));

            DeliveryTime deliveryTime1 = new DeliveryTime(
                    1,
                    LocalTime.of(9, 0),
                    LocalTime.of(12, 0)
            );

            DeliveryTime deliveryTime2 = new DeliveryTime(
                    2,
                    LocalTime.of(13, 0),
                    LocalTime.of(18, 0)
            );

            deliveryTimeRepository.saveAll(List.of(deliveryTime1, deliveryTime2));

            Region region = new Region("Tashkent");
            regionRepository.save(region);

            District district1 = new District("Yunusobod", region);
            District district2 = new District("Mirobod", region);
            District district3 = new District("Shayxontoxur", region);

            districtRepository.saveAll(List.of(district1, district2, district3));

            Role role1 = new Role(1, RoleName.ROLE_OPERATOR);
            Role role2 = new Role(2, RoleName.ROLE_CLIENT);
            Role role3 = new Role(3, RoleName.ROLE_DIRECTOR);
            Role role4 = new Role(4, RoleName.ROLE_COURIER);

            roleRepository.saveAll(List.of(role1, role2, role3, role4));

            User user = new User(
                    "+998901234567",
                    passwordEncoder.encode("1"),
                    List.of(role1)
            );
            userRepository.save(user);

//            orderRepository.saveAll(List.of(
//                    new Order(
//                            1,
//                            TelegramUser.builder()
//                                    .district(district3)
//                                    .build(),
//                            LocalDate.now(),
//                            OrderStatus.CREATED,
//                            deliveryTime2,
//                            null,
//                            10,
//                            bottleType1,
//                            new Location(41.322181650685025f, 69.20394975600072f)
//                    ),
//                    new Order(
//                            2,
//                            TelegramUser.builder().district(district3).build(),
//                            LocalDate.now(),
//                            OrderStatus.CREATED,
//                            deliveryTime2,
//                            null,
//                            10,
//                            bottleType1,
//                            new Location(41.33098550596806f, 69.2153989972744f)
//                    )
//            ));
//
//            List<Location> locations = List.of(
//                    new Location(41.322181650685025f, 69.20394975600072f),
//                    new Location(41.33098550596806f, 69.2153989972744f)
//            );
//
//            String url = distanceUtil.buildDirectionsApiUrl(
//                    locations,
//                    BotServiceImpl.companyLocation
//            );
//
//            String response = distanceUtil.getOptimizedRoute(locations, BotServiceImpl.companyLocation);
//            int[] optimizedOrder = distanceUtil.getOptimizedOrder(response);
//            System.out.println(Arrays.toString(optimizedOrder));
        }

    }
}
