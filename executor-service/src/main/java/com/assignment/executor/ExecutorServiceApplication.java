package com.assignment.executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableJpaRepositories("com.assignment.repository")
@EntityScan("com.assignment.entity")
@ComponentScan("com.assignment")
@OpenAPIDefinition(info = @Info(title = "Executor", version = "1.0", description = "API documentation for Executor Service"))
public class ExecutorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExecutorServiceApplication.class, args);
	}

}
