package com.example.schedule.schedule.dto;

import com.example.schedule.schedule.entity.TransOption;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransOptDto {
    private Long id;
    private double estimatedCost;
    private double travelTime;
    private Long routeId;
    private TransOption.TransMode mode;
public static TransOptDto fromEntity(TransOption entity) {
    return TransOptDto.builder()
            .id(entity.getId())
            .estimatedCost(entity.getEstimatedCost())
            .travelTime(entity.getTravelTime())
            .routeId(entity.getRoute().getId())
            .mode(entity.getMode())
            .build();
}



}
