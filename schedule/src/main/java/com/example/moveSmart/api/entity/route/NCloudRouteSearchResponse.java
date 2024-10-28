package com.example.moveSmart.api.entity.route;

import lombok.Data;
import java.util.List;

@Data
public class NCloudRouteSearchResponse {
    private int code; // Response code (e.g., 0 for success)
    private String message; // Message providing additional info (e.g., "길찾기를 성공하였습니다.")
    private Route route; // The route details

    @Data
    public static class Route {
        private List<TraOptimal> traoptimal; // List of optimal routes

        @Data
        public static class TraOptimal {
            private Summary summary; // Summary of the route

            @Data
            public static class Summary {
                private StartGoal start; // Start location details
                private Goal goal; // Goal location details
                private double distance; // Total distance of the route
                private double duration; // Total duration of the route
                private double tollFare; // Toll fare
                private double taxiFare; // Taxi fare
                private double fuelPrice; // Fuel price
                public double getDurationInMinutes() {
                    return duration / 1000 / 60;
                }
            }

            @Data
            public static class StartGoal {
                private List<Double> location; // Start location as [longitude, latitude]
            }

            @Data
            public static class Goal {
                private List<Double> location; // Goal location as [longitude, latitude]
                private int dir; // Direction
            }
        }
    }
}
