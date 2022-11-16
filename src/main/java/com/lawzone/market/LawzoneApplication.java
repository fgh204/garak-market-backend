package com.lawzone.market;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class LawzoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(LawzoneApplication.class, args);
	}

}