package com.example.schedule.schedule.plan.dto;

import com.example.schedule.schedule.route.entity.Location;
import com.example.schedule.schedule.plan.entity.Plan;
import com.example.schedule.schedule.route.entity.TransOption;
import com.example.schedule.user.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDto {
    private Long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Location startLocation;
    private Location destination;
    private TransOption.TransMode transportationMode;
    private double estimatedCost;
    private String notificationMessage;
    private boolean completed = false;
    private UserDto user;

    public static PlanDto fromEntity(Plan entity) {
        return fromEntity(entity, false);
    }

    public static PlanDto fromEntity(Plan entity, boolean withUser) {
        return PlanDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .startLocation(entity.getStartLocation())
                .destination(entity.getDestination())
                .transportationMode(entity.getMode())
                .estimatedCost(entity.getEstimatedCost())
                .notificationMessage(entity.getNotificationMessage())
                .completed(entity.isCompleted())
                .user(withUser ? UserDto.fromEntity(entity.getUser()) : null)
                .build();
    }
}