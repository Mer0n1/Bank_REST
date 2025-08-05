package com.example.bankcards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Bank_RESTApplication {
    public static void main(String[] args) {
        SpringApplication.run(Bank_RESTApplication.class, args);
    }
}
