package com.ems.event.management.service.service.impl;

import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.enums.Status;
import com.ems.event.management.service.repository.AttendanceRepository;
import com.ems.event.management.service.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository attendanceRepository;

    @Override
    public Attendance markAttendance(UUID eventId, UUID userId, Status status) {
        Attendance attendance = Attendance.builder()
                .eventId(eventId)
                .userId(userId)
                .status(status)
                .respondedAt(LocalDateTime.now())
                .build();

        return attendanceRepository.save(attendance);
    }

    @Override
    public List<Attendance> getAttendanceForUser(UUID userId) {
        return attendanceRepository.findByUserId(userId);
    }

    @Override
    public List<Attendance> getAttendanceForEvent(UUID eventId) {
        return attendanceRepository.findByEventId(eventId);
    }

    @Override
    public long countAttendees(UUID eventId) {
        return attendanceRepository.findByEventId(eventId).stream()
                .filter(a -> a.getStatus() == Status.GOING)
                .count();
    }
}
