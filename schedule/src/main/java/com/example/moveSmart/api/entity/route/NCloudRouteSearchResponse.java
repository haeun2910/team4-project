package com.example.moveSmart.api.entity.route;

import lombok.Data;
import java.util.List;

@Data
public class NCloudRouteSearchResponse {
    private int code; // Response code (e.g., 0 for success)
    private String message; // Message providing additional info (e.g., "길찾기를 성공하였습니다.")
    private String currentDateTime; // Current date and time of the response
    private Route route; // The route details

    @Data
    public static class Route {
        private List<TraOptimal> traoptimal; // List of optimal routes

        @Data
        public static class TraOptimal {
            private Summary summary; // Summary of the route
            private List<List<Double>> path; // Path coordinates
            private List<Section> section; // List of sections on the route
            private List<Guide> guide; // List of guidance steps

            @Data
            public static class Summary {
                private Start start; // Start location details
                private Goal goal; // Goal location details
                private double distance; // Total distance of the route
                private double duration; // Total duration of the route
                private String departureTime; // Departure time
                private List<List<Double>> bbox; // Bounding box for the route
                private double tollFare; // Toll fare
                private double taxiFare; // Taxi fare
                private double fuelPrice; // Fuel price

                public double getDurationInMinutes() {
                    return duration / 1000 / 60;
                }
            }

            @Data
            public static class Start {
                private List<Double> location; // Start location as [longitude, latitude]
            }

            @Data
            public static class Goal {
                private List<Double> location; // Goal location as [longitude, latitude]
                private int dir; // Direction
            }

            @Data
            public static class Section {
                private int pointIndex; // Starting point index of this section
                private int pointCount; // Number of points in this section
                private double distance; // Distance of the section
                private String name; // Name of the section
                private int congestion; // Congestion level
                private double speed; // Speed in the section
            }

            @Data
            public static class Guide {
                private int pointIndex; // Point index for the guidance step
                private int type; // Type of guidance (e.g., 3 for turn, 88 for destination)
                private String instructions; // Instructions for the guidance step
                private double distance; // Distance to the next step
                private double duration; // Duration to the next step
            }
        }
    }
}
