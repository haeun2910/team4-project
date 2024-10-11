package com.example.moveSmart.schedule.plan.dto;

import com.example.moveSmart.schedule.route.entity.Location;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.route.entity.TransOption;
import com.example.moveSmart.schedule.task.dto.TaskDto;
import com.example.moveSmart.user.dto.UserDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private List<PlanTaskDto> tasks;


    public static PlanDto fromEntity(Plan entity) {
        return fromEntity(entity, false);
    }

    public static PlanDto fromEntity(Plan entity, boolean withUser) {
        List<PlanTaskDto> taskDtos = (entity.getTasks() != null) ?
                entity.getTasks().stream()
                        .map(task -> PlanTaskDto.fromPlanTaskEntity(task, withUser))
                        .collect(Collectors.toList())
                : List.of();
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
                .tasks(taskDtos)
                .build();
    }
}