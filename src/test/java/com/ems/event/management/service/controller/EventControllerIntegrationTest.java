package com.ems.event.management.service.controller;

import com.ems.event.management.service.dto.EventRequestDTO;
import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.enums.Visibility;
import com.ems.event.management.service.repository.EventRepository;
import com.ems.event.management.service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID hostId;

    @BeforeEach
    void setup() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        var user = userRepository.save(
                com.ems.event.management.service.entity.User.builder()
                        .id(UUID.randomUUID())
                        .email("test@example.com")
                        .build()
        );
        hostId = user.getId();
    }

    @Test
    void createEvent_ShouldReturnCreated() throws Exception {
        EventRequestDTO requestDTO = EventRequestDTO.builder()
                .title("Test Event")
                .description("Description")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .location("Test Location")
                .visibility(Visibility.PUBLIC)
                .build();

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .header("Authorization", "Bearer " + hostId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    void getEvent_ShouldReturnEvent() throws Exception {
        Event event = Event.builder()
                .title("Sample Event")
                .description("Sample")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .location("Location")
                .visibility(Visibility.PUBLIC)
                .host(userRepository.findById(hostId).get())
                .build();
        Event saved = eventRepository.save(event);

        mockMvc.perform(get("/api/v1/events/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Sample Event"));
    }

    @Test
    void listUpcomingEvents_ShouldReturnList() throws Exception {
        mockMvc.perform(get("/api/v1/events/upcoming"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }
}
