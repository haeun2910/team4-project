package com.example.moveSmart.schedule.plan.dto;

import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.user.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonFormat;
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
//    private LocalDateTime startTime;
//    private LocalDateTime endTime;
//    private Location startLocation;
//    private Location destination;
//    private TransOption.TransMode transportationMode;
//    private double estimatedCost;
    private String departureName;
    private Double departureLat;
    private Double departureLng;
    private String arrivalName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime arrivalAt;
    private Double arrivalLat;
    private Double arrivalLng;
    private String notificationMessage;
    private boolean completed = false;
    private UserDto user;
    private List<PlanTaskDto> planTasks;


    public static PlanDto fromEntity(Plan entity) {
        return fromEntity(entity, false);
    }

    public static PlanDto fromEntity(Plan entity, boolean withUser) {
        List<PlanTaskDto> taskDtos = (entity.getPlanTasks() != null) ?
                entity.getPlanTasks().stream()
                        .map(planTask -> PlanTaskDto.fromPlanTaskEntity(planTask, withUser)) // pass PlanTask object directly
                        .collect(Collectors.toList())
                : List.of();
        return PlanDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
//                .startTime(entity.getStartTime())
//                .endTime(entity.getEndTime())
//                .startLocation(entity.getStartLocation())
//                .destination(entity.getDestination())
//                .transportationMode(entity.getMode())
//                .estimatedCost(entity.getEstimatedCost())
                .departureName(entity.getDepartureName())
                .departureLat(entity.getDepartureLat())
                .departureLng(entity.getDepartureLng())
                .arrivalName(entity.getArrivalName())
                .arrivalAt(entity.getArrivalAt())
                .arrivalLat(entity.getArrivalLat())
                .arrivalLng(entity.getArrivalLng())
                .notificationMessage(entity.getNotificationMessage())
                .completed(entity.isCompleted())
                .user(withUser ? UserDto.fromEntity(entity.getUser()) : null)
                .planTasks(taskDtos)
                .build();
    }
}