package com.meli.project.meliproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MeliProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeliProjectApplication.class, args);
    }

}
