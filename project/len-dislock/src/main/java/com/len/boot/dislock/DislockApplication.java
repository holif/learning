package com.len.boot.dislock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DislockApplication {

    public static void main(String[] args) {
        SpringApplication.run(DislockApplication.class, args);
    }

}
