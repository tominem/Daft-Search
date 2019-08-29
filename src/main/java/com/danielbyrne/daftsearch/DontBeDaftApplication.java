package com.danielbyrne.daftsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DontBeDaftApplication {

    public static void main(String[] args) {
        SpringApplication.run(DontBeDaftApplication.class, args);
    }
}
