package com.example.schedule.schedule.entity;

import com.example.schedule.entity.BaseEntity;
import com.example.schedule.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="schedule_table")
public class Schedule extends BaseEntity {
    @Setter
    private String title;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserEntity user;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Location startLocation;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Location destination;
    @Setter
    @FutureOrPresent(message = "Start time must be present or future")
    private LocalDateTime startTime;
    @Setter
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private TransOption.TransMode mode;
    @Setter
    private double estimatedCost;
    @Setter
    private String notificationMessage;

}
