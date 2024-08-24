package com.codenear.butterfly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class ButterflyApplication {

	public static void main(String[] args) {
		SpringApplication.run(ButterflyApplication.class, args);
	}

}
