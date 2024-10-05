package com.example.schedule.schedule.dto;

import com.example.schedule.schedule.entity.Schedule;
import com.example.schedule.schedule.entity.TransportationMode;
import com.example.schedule.user.entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String transportationMode;
    private double estimatedCost;
    private String notificationMessage;

    public static ScheduleDto fromEntity(Schedule entity) {
        return ScheduleDto.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .title(entity.getTitle())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .transportationMode(entity.getMode().name())
                .estimatedCost(entity.getEstimatedCost())
                .notificationMessage(entity.getNotificationMessage())
                .build();
    }
}
