package com.example.moveSmart.schedule.task.dto;

import com.example.moveSmart.schedule.plan.dto.PlanDto;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.user.dto.UserDto;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto {
    private Long id;
    private Long planId;
    private String title;
    private Integer time;
    private boolean completed = false;
    private UserDto user;

    public static TaskDto fromEntity(Task entity){
        return fromEntity(entity, false);
    }
    public static TaskDto fromEntity(Task entity, boolean withUser) {
        return TaskDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .completed(entity.isCompleted())
                .time(entity.getTime())
                .planId(entity.getPlan() != null ? entity.getPlan().getId() : null)
                .user(withUser ? UserDto.fromEntity(entity.getUser()) : null)
                .build();
    }
}
