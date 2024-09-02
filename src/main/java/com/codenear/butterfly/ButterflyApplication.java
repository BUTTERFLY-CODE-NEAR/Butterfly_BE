package com.codenear.butterfly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ButterflyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ButterflyApplication.class, args);
	}

}
