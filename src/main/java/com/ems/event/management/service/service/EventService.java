package com.ems.event.management.service.service;

import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventService {

    Event createEvent(Event event);

    Optional<Event> getEventById(UUID eventId);

    Event updateEvent(UUID eventId, Event updatedEvent);

    void archiveEvent(UUID eventId);  // Add method for archiving events

    boolean isHost(UUID eventId, UUID userId);

    Page<Event> listEventsByVisibility(Visibility visibility, Pageable pageable);

    Page<Event> listUpcomingEvents(Pageable pageable);

    List<Event> listEventsByHost(UUID hostId);

    void deleteEvent(UUID eventId);  // Optional: If you still want the option to hard delete events
}
