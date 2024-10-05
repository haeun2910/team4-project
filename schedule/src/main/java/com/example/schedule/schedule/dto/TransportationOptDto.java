package com.example.schedule.schedule.dto;

import com.example.schedule.schedule.entity.TransportationOption;
import lombok.*;

import java.util.List;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransportationOptDto {
    private Long id;
    private double estimatedCost;
    private double travelTime;
    private Long routeId;
    private String mode;
public static TransportationOptDto fromEnity(TransportationOption entity) {
    return TransportationOptDto.builder()
            .id(entity.getId())
            .estimatedCost(entity.getEstimatedCost())
            .travelTime(entity.getTravelTime())
            .routeId(entity.getRoute().getId())
            .mode(entity.getMode().name())
            .build();
}



}
