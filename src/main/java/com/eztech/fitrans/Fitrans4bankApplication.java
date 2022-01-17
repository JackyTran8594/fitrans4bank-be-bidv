package com.eztech.fitrans;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Fitrans4bankApplication {

	public static void main(String[] args) {
		SpringApplication.run(Fitrans4bankApplication.class, args);
	}
}
