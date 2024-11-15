package com.lotte4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
@EnableCaching
@EnableScheduling
@EnableMongoAuditing // 2024.11.07 추가 - 황수빈
@SpringBootApplication
public class LotteonTeam4Application {

    public static void main(String[] args) {
        SpringApplication.run(LotteonTeam4Application.class, args);
    }

}
