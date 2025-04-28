package com.ems.event.management.service.controller;

import com.ems.event.management.service.dto.AttendanceRequestDTO;
import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.enums.Status;
import com.ems.event.management.service.repository.AttendanceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AttendanceControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UUID eventId;

    @BeforeEach
    void setup() {
        attendanceRepository.deleteAll();
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();
    }

    @Test
    void testMarkAttendance() throws Exception {
        AttendanceRequestDTO request = new AttendanceRequestDTO();
        request.setStatus(Status.GOING);

        mockMvc.perform(post("/api/v1/attendance/" + eventId)
                        .principal(() -> userId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(eventId.toString()))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.status").value("GOING"));
    }

    @Test
    void testGetUserAttendances() throws Exception {
        Attendance attendance = Attendance.builder()
                .eventId(eventId)
                .userId(userId)
                .status(Status.GOING)
                .respondedAt(java.time.LocalDateTime.now())
                .build();
        attendanceRepository.save(attendance);

        mockMvc.perform(get("/api/v1/attendance/user")
                        .principal(() -> userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(userId.toString())));
    }
}
