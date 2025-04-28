package com.ems.event.management.service.service.impl;

import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.enums.Visibility;
import com.ems.event.management.service.exception.ResourceNotFoundException;
import com.ems.event.management.service.repository.EventRepository;
import com.ems.event.management.service.service.EventService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    @RateLimiter(name = "default", fallbackMethod = "rateLimitExceeded")
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    @Cacheable(value = "eventDetails", key = "#eventId")
    public Optional<Event> getEventById(UUID eventId) {
        return eventRepository.findById(eventId)
                .filter(e -> !e.isArchived());  // Filter out archived events
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "upcomingEvents", allEntries = true),
            @CacheEvict(value = "eventDetails", key = "#eventId")
    })
    public Event updateEvent(UUID eventId, Event updatedEvent) {
        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setStartTime(updatedEvent.getStartTime());
        existingEvent.setEndTime(updatedEvent.getEndTime());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setVisibility(updatedEvent.getVisibility());

        return eventRepository.save(existingEvent);
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
    public boolean isHost(UUID eventId, UUID userId) {
        return eventRepository.findById(eventId)
                .filter(event -> event.getHost().getId().equals(userId))
                .isPresent();
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

    @Override
    @Caching(evict = {
            @CacheEvict(value = "upcomingEvents", allEntries = true),
            @CacheEvict(value = "eventDetails", key = "#eventId")
    })
    public void deleteEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setArchived(true);
        eventRepository.save(event);
    }

    // Fallback method when rate limit is exceeded
    public Event rateLimitExceeded(UUID eventId, Event event, Throwable throwable) {
        throw new IllegalStateException("Rate limit exceeded, please try again later.");
    }
}
