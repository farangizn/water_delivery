package org.example.waterdelivery;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@SpringBootApplication
public class WaterdeliveryApplication {

    @Value("${bot.token}")
    private String botToken;

    public static void main(String[] args) {
        SpringApplication.run(WaterdeliveryApplication.class, args);
    }

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
