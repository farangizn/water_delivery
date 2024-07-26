package org.example.waterdelivery.repo;

import org.example.waterdelivery.entity.TelegramUser;
import org.example.waterdelivery.entity.enums.TelegramState;
import org.example.waterdelivery.projection.SimpleWaitingUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, UUID> {

    Optional<TelegramUser> findByChatId(Long chatId);

    List<SimpleWaitingUser> findAllByStateOrderByCreatedAtAsc(TelegramState state);

    TelegramUser findByUserId(UUID userId);
}