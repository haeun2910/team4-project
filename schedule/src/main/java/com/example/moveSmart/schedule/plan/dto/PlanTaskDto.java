package com.example.moveSmart.schedule.plan.dto;

import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.entity.PlanTask;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.user.dto.UserDto;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class PlanTaskDto {
    private Long id;
    private Long planId;
    private String title;
    private Integer time;
    private boolean completed = false;
    private UserDto user;
    private PlanDto plan;


    public static PlanTaskDto fromPlanTaskEntity(PlanTask entity, boolean withUser) {
        Task task = entity.getTask();
        return PlanTaskDto.builder()
                .id(task.getId())
                .planId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .title(task.getTitle())
                .time(task.getTime())
                .completed(task.isCompleted())
                .user(withUser ? UserDto.fromEntity(entity.getUser()) : null)
                // Avoid setting PlanDto here to prevent recursion
                .build();
    }
}