package com.ems.event.management.service.controller;

import com.ems.event.management.service.dto.AttendanceRequestDTO;
import com.ems.event.management.service.dto.CountDTO;
import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/{eventId}")
    public ResponseEntity<EntityModel<Attendance>> markAttendance(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UUID eventId,
            @Valid @RequestBody AttendanceRequestDTO request) {

        Attendance attendance = attendanceService.markAttendance(eventId, userId, request.getStatus());

        EntityModel<Attendance> model = EntityModel.of(attendance,
                linkTo(methodOn(AttendanceController.class).markAttendance(userId, eventId, request)).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }

    @GetMapping("/user")
    public ResponseEntity<CollectionModel<EntityModel<Attendance>>> getUserAttendances(@AuthenticationPrincipal UUID userId) {
        List<Attendance> attendances = attendanceService.getAttendanceForUser(userId);

        List<EntityModel<Attendance>> models = attendances.stream()
                .map(attendance -> EntityModel.of(attendance,
                        linkTo(methodOn(AttendanceController.class).getUserAttendances(userId)).withSelfRel()
                ))
                .toList();

        return ResponseEntity.ok(CollectionModel.of(models));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<CollectionModel<EntityModel<Attendance>>> getEventAttendances(@PathVariable UUID eventId) {
        List<Attendance> attendances = attendanceService.getAttendanceForEvent(eventId);

        List<EntityModel<Attendance>> models = attendances.stream()
                .map(attendance -> EntityModel.of(attendance,
                        linkTo(methodOn(AttendanceController.class).getEventAttendances(eventId)).withSelfRel()
                ))
                .toList();

        return ResponseEntity.ok(CollectionModel.of(models));
    }

    @GetMapping("/event/{eventId}/count")
    public ResponseEntity<EntityModel<CountDTO>> countEventAttendees(@PathVariable UUID eventId) {
        long attendeeCount = attendanceService.countAttendees(eventId);

        CountDTO countDTO = new CountDTO(attendeeCount);

        EntityModel<CountDTO> model = EntityModel.of(countDTO,
                linkTo(methodOn(AttendanceController.class).countEventAttendees(eventId)).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }

}
