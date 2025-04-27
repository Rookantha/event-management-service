package com.ems.event.management.service.repository;

import com.ems.event.management.service.entity.Attendance;
import com.ems.event.management.service.entity.AttendanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, AttendanceId> {

    List<Attendance> findByUserId(UUID userId);

    List<Attendance> findByEventId(UUID eventId);
}
