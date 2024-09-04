package com.project.react_tft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ReactTftApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactTftApplication.class, args);
    }

}
