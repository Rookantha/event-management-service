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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final AttendanceService attendanceService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EntityModel<EventResponseDTO>> createEvent(
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

        EntityModel<EventResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEvent(saved.getId())).withSelfRel(),
                linkTo(methodOn(EventController.class).updateEvent(saved.getId(), eventRequestDTO)).withRel("update"),
                linkTo(methodOn(EventController.class).archiveEvent(saved.getId())).withRel("archive")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EntityModel<EventResponseDTO>> getEvent(@PathVariable UUID eventId) {
        Event event = eventService.getEventById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        long count = attendanceService.countAttendees(eventId);

        EventResponseDTO response = mapToResponse(event, count);

        EntityModel<EventResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEvent(eventId)).withSelfRel(),
                linkTo(methodOn(EventController.class).listUpcomingEvents(0, 10)).withRel("upcoming-events")
        );

        return ResponseEntity.ok(model);
    }

    @GetMapping("/upcoming")
    public ResponseEntity<CollectionModel<EntityModel<EventResponseDTO>>> listUpcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        Page<Event> events = eventService.listUpcomingEvents(pageable);

        List<EntityModel<EventResponseDTO>> models = events.stream()
                .map(event -> EntityModel.of(mapToResponse(event, 0),
                        linkTo(methodOn(EventController.class).getEvent(event.getId())).withSelfRel()
                ))
                .toList();

        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(EventController.class).listUpcomingEvents(page, size)).withSelfRel()
        ));
    }

    @PatchMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or @eventServiceImpl.isHost(#eventId, principal.id)")
    public ResponseEntity<EntityModel<EventResponseDTO>> updateEvent(
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

        EventResponseDTO response = mapToResponse(saved, count);

        EntityModel<EventResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(EventController.class).getEvent(saved.getId())).withSelfRel()
        );

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN') or @eventServiceImpl.isHost(#eventId, principal.id)")
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
