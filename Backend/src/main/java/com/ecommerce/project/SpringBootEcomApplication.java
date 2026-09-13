package com.ecommerce.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EntityScan("com.ecommerce.project.model")
@ComponentScan(basePackages = "com.ecommerce")
public class SpringBootEcomApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootEcomApplication.class, args);
	}

}
