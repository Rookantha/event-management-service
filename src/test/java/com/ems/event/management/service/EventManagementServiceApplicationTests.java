package com.ems.event.management.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@Import(TestcontainersConfiguration.class)
@ActiveProfiles("local")
@SpringBootTest
class EventManagementServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
