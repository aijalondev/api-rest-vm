package com.api_rest_vm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.api_rest_vm")
public class ApiRestVmApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRestVmApplication.class, args);
	}

}
