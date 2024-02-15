package io.github.axelfrache.savesyncserver;

import io.github.axelfrache.savesyncserver.service.FileStorageService;
import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SavesyncServerApplication {

    @Resource
    FileStorageService storageService;

    public static void main(String[] args) {
        SpringApplication.run(SavesyncServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() throws Exception {
        return args -> {
            storageService.init();
        };
    }
}