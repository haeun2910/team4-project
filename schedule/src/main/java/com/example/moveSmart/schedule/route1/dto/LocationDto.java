package com.example.moveSmart.schedule.route.dto;

import com.example.moveSmart.schedule.route.entity.Location;
import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {
    private Long id;
    private String locationName;
    private String address;
    private double latitude;
    private double longitude;
    public static LocationDto fromEntity(Location locationEntity) {
        return LocationDto.builder()
                .id(locationEntity.getId())
                .locationName(locationEntity.getLocationName())
                .address(locationEntity.getAddress())
                .latitude(locationEntity.getLatitude())
                .longitude(locationEntity.getLongitude())
                .build();
    }
}
