package com.ems.event.management.service.entity;

import com.ems.event.management.service.enums.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendances")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AttendanceId.class)
public class Attendance {
    @Id
    private UUID eventId;

    @Id
    private UUID userId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime respondedAt;
}
