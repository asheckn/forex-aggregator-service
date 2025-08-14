package com.wiremit.forex_aggregator_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ForexAggregatorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForexAggregatorServiceApplication.class, args);
	}

}
