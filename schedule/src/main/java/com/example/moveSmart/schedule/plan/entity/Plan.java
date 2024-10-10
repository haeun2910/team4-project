package com.example.moveSmart.schedule.plan.entity;


import com.example.moveSmart.entity.BaseEntity;
import com.example.moveSmart.schedule.route.entity.Location;
import com.example.moveSmart.schedule.route.entity.TransOption;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="plan_table")
public class Plan extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Setter
    private String title;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Location startLocation;
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Location destination;
    @Setter
//    @FutureOrPresent(message = "Start time must be present or future")
    private LocalDateTime startTime;
    @Setter
    @Future(message = "End time must be in the future")
    private LocalDateTime endTime;
    @Setter
    @Enumerated(EnumType.STRING)
    private TransOption.TransMode mode;
    @Setter
    private double estimatedCost;
    @Setter
    private String notificationMessage;
    @Setter
    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private boolean completed;
    @OneToMany(mappedBy = "plan")
    private final List<Task> tasks = new ArrayList<>();

}
