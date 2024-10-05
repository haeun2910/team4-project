package com.example.schedule.schedule.dto;

import com.example.schedule.schedule.entity.Route;
import com.example.schedule.schedule.entity.TransportationOption;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

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

