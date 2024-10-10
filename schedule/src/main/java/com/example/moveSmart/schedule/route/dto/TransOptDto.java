package com.example.moveSmart.schedule.route.dto;

import com.example.moveSmart.schedule.route.entity.TransOption;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransOptDto {
    private Long id;
    private double estimatedCost;
    private Long routeId;
    private TransOption.TransMode mode;
public static TransOptDto fromEntity(TransOption entity) {
    return TransOptDto.builder()
            .id(entity.getId())
            .estimatedCost(entity.getEstimatedCost())
            .routeId(entity.getRoute().getId())
            .mode(entity.getMode())
            .build();
}



}
