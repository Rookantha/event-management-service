package com.ems.event.management.service.service.impl;


import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.enums.Visibility;
import com.ems.event.management.service.exception.ResourceNotFoundException;
import com.ems.event.management.service.repository.EventRepository;
import com.ems.event.management.service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    @Cacheable(value = "eventDetails", key = "#eventId")
    public Optional<Event> getEventById(UUID eventId) {
        return eventRepository.findById(eventId)
                .filter(e -> !e.isArchived());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "upcomingEvents", allEntries = true),
            @CacheEvict(value = "eventDetails", key = "#eventId")
    })
    public Event updateEvent(UUID eventId, Event updatedEvent) {
        Event existing = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        existing.setTitle(updatedEvent.getTitle());
        existing.setDescription(updatedEvent.getDescription());
        existing.setStartTime(updatedEvent.getStartTime());
        existing.setEndTime(updatedEvent.getEndTime());
        existing.setLocation(updatedEvent.getLocation());
        existing.setVisibility(updatedEvent.getVisibility());

        return eventRepository.save(existing);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "upcomingEvents", allEntries = true),
            @CacheEvict(value = "eventDetails", key = "#eventId")
    })
    public void archiveEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setArchived(true);
        eventRepository.save(event);
    }


    @Override
    public Page<Event> listEventsByVisibility(Visibility visibility, Pageable pageable) {
        return eventRepository.findByVisibilityAndArchivedFalse(visibility, pageable);
    }

    @Override
    @Cacheable(value = "upcomingEvents", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Event> listUpcomingEvents(Pageable pageable) {
        return eventRepository.findUpcomingEvents(pageable);
    }

    @Override
    public List<Event> listEventsByHost(UUID hostId) {
        return eventRepository.findByHostId(hostId);
    }
}
