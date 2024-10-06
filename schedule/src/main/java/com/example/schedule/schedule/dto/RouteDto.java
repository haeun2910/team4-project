package com.example.schedule.schedule.dto;

import com.example.schedule.schedule.entity.Route;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {
    private Long id;
    private String startLocation;
    private String destination;
    private double distance;
    private double estimatedTime;

    public RouteDto fromEntity(Route entity) {
        return RouteDto.builder()
                .id(entity.getId())
                .startLocation(entity.getStartLocation().getLocationName())
                .destination(entity.getDestination().getLocationName())
                .distance(entity.getDistance())
                .estimatedTime(entity.getEstimatedTime())
                .build();
    }
}

