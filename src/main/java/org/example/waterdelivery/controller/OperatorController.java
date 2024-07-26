package org.example.waterdelivery.controller;

import lombok.RequiredArgsConstructor;
import org.example.waterdelivery.bot.BotService;
import org.example.waterdelivery.dto.Location;
import org.example.waterdelivery.dto.UserInfoDTO;
import org.example.waterdelivery.dto.UserVerificationDTO;
import org.example.waterdelivery.entity.District;
import org.example.waterdelivery.entity.TelegramUser;
import org.example.waterdelivery.entity.User;
import org.example.waterdelivery.entity.enums.TelegramState;
import org.example.waterdelivery.projection.SimpleWaitingUser;
import org.example.waterdelivery.repo.DistrictRepository;
import org.example.waterdelivery.repo.TelegramUserRepository;
import org.example.waterdelivery.utils.DistrictUtil;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/operator")
@RequiredArgsConstructor
public class OperatorController {

    private final TelegramUserRepository telegramUserRepository;
    private final DistrictRepository districtRepository;
    private final DistrictUtil districtUtil;
    private final BotService botService;

    @GetMapping
    public String operatorPage(Model model) {
        List<SimpleWaitingUser> waitingUsers = telegramUserRepository.findAllByStateOrderByCreatedAtAsc(TelegramState.WAITING);
        model.addAttribute("users", waitingUsers);
        return "operator/index";
    }

    @Transactional
    @GetMapping("/userverify/{tgUserId}")
    public String verifyUser(@PathVariable UUID tgUserId, Model model) {
        TelegramUser telegramUser = telegramUserRepository.findById(tgUserId).orElseThrow(() -> new RuntimeException("Telegram User not found"));
        model.addAttribute("user", telegramUser);
        model.addAttribute("districts", districtRepository.findAllByRegionId(telegramUser.getRegion().getId()));
        String districtName = districtUtil.getDistrictName(telegramUser.getLocation());
        System.out.println(districtName);
        District district = districtRepository.findSimilarDistrictName(districtName);
//        System.out.println(district.toString());
        model.addAttribute("currentDistrict", district);
        return "/operator/verify";
    }

    @PostMapping("wronglocation")
    public String wrongLocation(@RequestParam(name = "userId") UUID tgUserId) {
        TelegramUser tgUser = telegramUserRepository.findById(tgUserId).orElseThrow(() -> new RuntimeException("Telegram User not found"));
        botService.sendLocation(tgUser);
        return "redirect:/operator";
    }

    @Transactional
    @PostMapping("verify")
    public String verify(@ModelAttribute UserVerificationDTO userVerificationDTO) {
        TelegramUser tgUser = telegramUserRepository.findById(userVerificationDTO.getTgUserId()).orElseThrow(() -> new RuntimeException("Telegram User not found"));
        tgUser.setLocation(new Location(
                userVerificationDTO.getLatitude(),
                userVerificationDTO.getLongitude()
        ));
        tgUser.setAddressLine(userVerificationDTO.getAddressLine());
        District district = districtRepository.findById(userVerificationDTO.getDistrictId()).orElseThrow(() -> new RuntimeException("District not found"));
        tgUser.setDistrict(district);
        tgUser.setVerified(true);
        tgUser.setState(TelegramState.CABINET);
        telegramUserRepository.save(tgUser);
        botService.sendCabinet(tgUser);
        return "redirect:/operator";
    }
}
