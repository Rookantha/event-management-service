package com.ems.event.management.service.service.impl;


import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.entity.User;
import com.ems.event.management.service.enums.Visibility;
import com.ems.event.management.service.exception.ResourceNotFoundException;
import com.ems.event.management.service.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceImplTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_ShouldSaveEvent() {
        Event event = createSampleEvent();
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event savedEvent = eventService.createEvent(event);

        assertThat(savedEvent).isNotNull();
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void getEventById_ShouldReturnEvent_WhenFoundAndNotArchived() {
        Event event = createSampleEvent();
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventById(event.getId());

        assertThat(result).isPresent();
    }

    @Test
    void getEventById_ShouldReturnEmpty_WhenArchived() {
        Event event = createSampleEvent();
        event.setArchived(true);
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        Optional<Event> result = eventService.getEventById(event.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void updateEvent_ShouldUpdateAndReturnUpdatedEvent() {
        Event event = createSampleEvent();
        Event updated = createSampleEvent();
        updated.setTitle("Updated Title");

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(updated);

        Event result = eventService.updateEvent(event.getId(), updated);

        assertThat(result.getTitle()).isEqualTo("Updated Title");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void updateEvent_ShouldThrow_WhenNotFound() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> eventService.updateEvent(eventId, createSampleEvent()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void archiveEvent_ShouldMarkEventAsArchived() {
        Event event = createSampleEvent();
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        eventService.archiveEvent(event.getId());

        assertThat(event.isArchived()).isTrue();
        verify(eventRepository).save(event);
    }

    @Test
    void isHost_ShouldReturnTrue_WhenUserIsHost() {
        Event event = createSampleEvent();
        UUID userId = event.getHost().getId();
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        boolean result = eventService.isHost(event.getId(), userId);

        assertThat(result).isTrue();
    }

    @Test
    void isHost_ShouldReturnFalse_WhenNotHost() {
        Event event = createSampleEvent();
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        boolean result = eventService.isHost(event.getId(), UUID.randomUUID());

        assertThat(result).isFalse();
    }

    @Test
    void listEventsByVisibility_ShouldReturnEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        when(eventRepository.findByVisibilityAndArchivedFalse(Visibility.PUBLIC, pageable))
                .thenReturn(Page.empty());

        Page<Event> result = eventService.listEventsByVisibility(Visibility.PUBLIC, pageable);

        assertThat(result).isNotNull();
    }

    @Test
    void listUpcomingEvents_ShouldReturnUpcomingEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        when(eventRepository.findUpcomingEvents(pageable)).thenReturn(Page.empty());

        Page<Event> result = eventService.listUpcomingEvents(pageable);

        assertThat(result).isNotNull();
    }

    private Event createSampleEvent() {
        return Event.builder()
                .id(UUID.randomUUID())
                .title("Sample Event")
                .description("Description")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .host(User.builder().id(UUID.randomUUID()).build())
                .visibility(Visibility.PUBLIC)
                .archived(false)
                .build();
    }
}
