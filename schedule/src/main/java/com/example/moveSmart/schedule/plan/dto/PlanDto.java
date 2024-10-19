package com.example.moveSmart.schedule.plan.dto;

import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.entity.PlanTask;
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
        return PlanDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
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
                .planTasks(convertToPlanTaskDtos(entity.getPlanTasks(), withUser))
                .build();
    }

    private static List<PlanTaskDto> convertToPlanTaskDtos(List<PlanTask> planTasks, boolean withUser) {
        return planTasks == null ? List.of() :
                planTasks.stream()
                        .map(task -> PlanTaskDto.fromPlanTaskEntity(task, withUser))
                        .collect(Collectors.toList());
    }
}