package com.gachon.learningmate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class LearningMateApplication {

    public static void main(String[] args) {
        SpringApplication.run(LearningMateApplication.class, args);
    }

}
