package com.example.join32;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.join32.domain")
public class Join32Application {

    public static void main(String[] args) {
        SpringApplication.run(Join32Application.class, args);
    }
}
