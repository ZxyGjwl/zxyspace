package com.zxyspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ZxySpaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZxySpaceApplication.class, args);
    }
}