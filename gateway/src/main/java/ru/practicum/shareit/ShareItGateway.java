package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItGateway {

    public static void main(String[] args) {
        System.setProperty("server.address", "0.0.0.0");
        SpringApplication.run(ShareItGateway.class, args);
    }
}