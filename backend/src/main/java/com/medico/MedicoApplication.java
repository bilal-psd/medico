package com.medico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MedicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicoApplication.class, args);
    }
}

