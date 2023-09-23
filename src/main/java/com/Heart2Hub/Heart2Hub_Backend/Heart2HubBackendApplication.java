package com.Heart2Hub.Heart2Hub_Backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Heart2HubBackendApplication {

	public static final Logger logger = LogManager.getLogger(Heart2HubBackendApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(Heart2HubBackendApplication.class, args);
	}

}
