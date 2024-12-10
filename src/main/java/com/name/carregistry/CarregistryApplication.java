package com.name.carregistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class CarregistryApplication {

	public static void main(String[] args) {
		propertytest propertytest = new propertytest();
		Logger logger = LoggerFactory.getLogger(CarregistryApplication.class);
		logger.info("Hola mundo");
		SpringApplication.run(CarregistryApplication.class, args);
	}

}
