package com.ems.event.management.service.repository;

import com.ems.event.management.service.entity.Event;
import com.ems.event.management.service.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    // Find events with specific visibility and not archived
    Page<Event> findByVisibilityAndArchivedFalse(Visibility visibility, Pageable pageable);

    // Find events by host ID and not archived
    @Query("SELECT e FROM Event e WHERE e.host.id = :hostId AND e.archived = false")
    List<Event> findByHostId(UUID hostId);

    // List upcoming events (startTime after now)
    @Query("SELECT e FROM Event e WHERE e.startTime > CURRENT_TIMESTAMP AND e.archived = false")
    Page<Event> findUpcomingEvents(Pageable pageable);

    // Find events that are archived
    Page<Event> findByArchivedTrue(Pageable pageable);
}
