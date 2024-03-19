package com.gachon.learningmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class LearningMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningMateApplication.class, args);
    }

}
