package com.ems.event.management.service.service.impl;

import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.enums.Status;
import com.ems.event.management.service.repository.AttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceServiceImplTest {

    private AttendanceRepository attendanceRepository;
    private AttendanceServiceImpl attendanceService;

    @BeforeEach
    void setUp() {
        attendanceRepository = mock(AttendanceRepository.class);
        attendanceService = new AttendanceServiceImpl(attendanceRepository);
    }

    @Test
    void testMarkAttendance() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Status status = Status.GOING;

        Attendance attendance = Attendance.builder()
                .eventId(eventId)
                .userId(userId)
                .status(status)
                .respondedAt(LocalDateTime.now())
                .build();

        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        Attendance result = attendanceService.markAttendance(eventId, userId, status);

        assertEquals(eventId, result.getEventId());
        assertEquals(userId, result.getUserId());
        assertEquals(status, result.getStatus());
        assertNotNull(result.getRespondedAt());

        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    void testGetAttendanceForUser() {
        UUID userId = UUID.randomUUID();
        List<Attendance> mockList = Arrays.asList(
                Attendance.builder().userId(userId).build()
        );

        when(attendanceRepository.findByUserId(userId)).thenReturn(mockList);

        List<Attendance> result = attendanceService.getAttendanceForUser(userId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());

        verify(attendanceRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testGetAttendanceForEvent() {
        UUID eventId = UUID.randomUUID();
        List<Attendance> mockList = Arrays.asList(
                Attendance.builder().eventId(eventId).build()
        );

        when(attendanceRepository.findByEventId(eventId)).thenReturn(mockList);

        List<Attendance> result = attendanceService.getAttendanceForEvent(eventId);

        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).getEventId());

        verify(attendanceRepository, times(1)).findByEventId(eventId);
    }

    @Test
    void testCountAttendees() {
        UUID eventId = UUID.randomUUID();
        List<Attendance> mockList = Arrays.asList(
                Attendance.builder().eventId(eventId).status(Status.GOING).build(),
                Attendance.builder().eventId(eventId).status(Status.DECLINED).build(),
                Attendance.builder().eventId(eventId).status(Status.MAYBE).build()
        );

        when(attendanceRepository.findByEventId(eventId)).thenReturn(mockList);

        long count = attendanceService.countAttendees(eventId);

        assertEquals(2, count);
        verify(attendanceRepository, times(1)).findByEventId(eventId);
    }
}
