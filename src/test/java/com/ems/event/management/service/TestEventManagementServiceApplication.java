package com.ems.event.management.service;

import org.springframework.boot.SpringApplication;

public class TestEventManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(EventManagementServiceApplication::main).with(TestcontainersConfiguration.class).run(args);

	}

}
