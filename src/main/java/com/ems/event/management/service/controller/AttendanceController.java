package com.ems.event.management.service.controller;

import com.ems.event.management.service.dto.AttendanceRequestDTO;
import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/{eventId}")
    public ResponseEntity<Attendance> markAttendance(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID eventId,
            @Valid @RequestBody AttendanceRequestDTO request) {

        Attendance attendance = attendanceService.markAttendance(eventId, userId, request.getStatus());
        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Attendance>> getUserAttendances(@AuthenticationPrincipal UUID userId) {
        List<Attendance> attendances = attendanceService.getAttendanceForUser(userId);
        return ResponseEntity.ok(attendances);
    }
}
