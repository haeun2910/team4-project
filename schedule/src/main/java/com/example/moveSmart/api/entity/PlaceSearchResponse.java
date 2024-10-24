package com.example.moveSmart.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PlaceSearchResponse {
    private List<Place> places;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Place {
        private String name;
        private double latitude;
        private double longitude;
    }
}
