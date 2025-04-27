package com.ems.event.management.service.controller;


import com.ems.event.management.service.dto.EventRequestDTO;
import com.ems.event.management.service.dto.EventResponseDTO;
import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.entity.User;
import com.ems.event.management.service.repository.UserRepository;
import com.ems.event.management.service.service.AttendanceService;
import com.ems.event.management.service.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(
            @AuthenticationPrincipal UUID userId,
            @Valid @RequestBody EventRequestDTO eventRequestDTO) {


        User host = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Event event = Event.builder()
                .title(eventRequestDTO.getTitle())
                .description(eventRequestDTO.getDescription())
                .startTime(eventRequestDTO.getStartTime())
                .endTime(eventRequestDTO.getEndTime())
                .location(eventRequestDTO.getLocation())
                .visibility(eventRequestDTO.getVisibility())
                .host(host)
                .build();

        Event saved = eventService.createEvent(event);
        EventResponseDTO response = mapToResponse(saved, 0);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> getEvent(@PathVariable UUID eventId) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        long count = attendanceService.countAttendees(eventId);

        return ResponseEntity.ok(mapToResponse(event, count));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<Page<EventResponseDTO>> listUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        Page<Event> events = eventService.listUpcomingEvents(pageable);

        Page<EventResponseDTO> response = events.map(event -> mapToResponse(event, 0));
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponseDTO> updateEvent(
            @PathVariable UUID eventId,
            @Valid @RequestBody EventRequestDTO eventRequestDTO) {

        Event updated = Event.builder()
                .title(eventRequestDTO.getTitle())
                .description(eventRequestDTO.getDescription())
                .startTime(eventRequestDTO.getStartTime())
                .endTime(eventRequestDTO.getEndTime())
                .location(eventRequestDTO.getLocation())
                .visibility(eventRequestDTO.getVisibility())
                .build();

        Event saved = eventService.updateEvent(eventId, updated);
        long count = attendanceService.countAttendees(eventId);

        return ResponseEntity.ok(mapToResponse(saved, count));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> archiveEvent(@PathVariable UUID eventId) {
        eventService.archiveEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    private EventResponseDTO mapToResponse(Event event, long count) {
        return EventResponseDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .visibility(event.getVisibility())
                .hostId(event.getHost().getId())
                .attendeeCount(count)
                .build();
    }
}
