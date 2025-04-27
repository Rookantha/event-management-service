package com.ems.event.management.service.service;

import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.enums.Status;

import java.util.List;
import java.util.UUID;

public interface AttendanceService {
    Attendance markAttendance(UUID eventId, UUID userId, Status status);

    List<Attendance> getAttendanceForUser(UUID userId);

    List<Attendance> getAttendanceForEvent(UUID eventId);

    long countAttendees(UUID eventId);
}
