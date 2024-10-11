package com.example.moveSmart.schedule.plan.dto;

import com.example.moveSmart.schedule.plan.entity.Plan;
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
    private boolean completed = false;
    private UserDto user;
    private PlanDto plan;


    public static PlanTaskDto fromPlanTaskEntity(Task entity, boolean withUser) {
        return PlanTaskDto.builder()
                .id(entity.getId())
                .planId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .title(entity.getTitle())
                .completed(entity.isCompleted())
                .user(withUser ? UserDto.fromEntity(entity.getUser()) : null)
//                .plan(entity.getPlan() != null ? PlanDto.fromEntity(entity.getPlan()) : null)
                .build();
    }
}