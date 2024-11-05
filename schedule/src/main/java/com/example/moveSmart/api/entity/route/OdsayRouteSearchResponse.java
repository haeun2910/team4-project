package com.example.moveSmart.api.entity.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OdsayRouteSearchResponse {
    private Result result;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Result {
        private int searchType;
        private int outTrafficCheck;
        private int busCount;
        private int subwayCount;
        private int subwayBusCount;
        private int pointDistance;
        private int startRadius;
        private int endRadius;
        private List<Path> path; // Changed from array to List

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        public static class Path {
            private int pathType;
            private Info info;
            private List<SubPath> subPath; // List of SubPath

            @NoArgsConstructor
            @AllArgsConstructor
            @Getter
            public static class Info {
                private double trafficDistance; // Khoảng cách giao thông
                private int totalWalk; // Tổng thời gian đi bộ
                private int totalTime; // Tổng thời gian di chuyển
                private int payment; // Chi phí
                private int busTransitCount; // Số lần chuyển xe buýt
                private int subwayTransitCount; // Number of subway transits
                private String mapObj; // The map object representation
                private String firstStartStation; // First start station name
                private String lastEndStation; // Last end station name
                private int totalStationCount; // Total number of stations
                private int busStationCount; // Number of bus stations
                private int subwayStationCount; // Number of subway stations
                private double totalDistance; // Total distance
                private int totalWalkTime; // Total walking time
            }

            @NoArgsConstructor
            @AllArgsConstructor
            @Getter
            public static class SubPath {
                private int trafficType; // Type of traffic
                private int distance; // Distance of the sub-path
                private int sectionTime; // Time taken for this section
                private int stationCount; // Number of stations in this sub-path
                private List<Lane> lane; // List of lanes
                private String startName; // Starting station name
                private double startX; // Starting X coordinate
                private double startY; // Starting Y coordinate
                private String endName; // Ending station name
                private double endX; // Ending X coordinate
                private double endY; // Ending Y coordinate
                private String way; // Direction/way
                private int wayCode; // Code for the way
                private String door; // Door information
                private int startID; // Starting station ID
                private int endID; // Ending station ID
                private String startExitNo; // Exit number at start
                private double startExitX; // Exit X coordinate at start
                private double startExitY; // Exit Y coordinate at start
                private String endExitNo; // Exit number at end
                private double endExitX; // Exit X coordinate at end
                private double endExitY; // Exit Y coordinate at end
                private PassStopList passStopList; // List of passed stops

                @NoArgsConstructor
                @AllArgsConstructor
                @Getter
                public static class Lane {
                    private String name; // Name of the lane
                    private int subwayCode; // Subway code
                    private int subwayCityCode; // City code for the subway
                    private String busNo; // Bus number
                    private int type; // Type of lane
                    private int busID; // Bus ID
                }

                @NoArgsConstructor
                @AllArgsConstructor
                @Getter
                public static class PassStopList {
                    private List<Station> stations; // List of stations

                    @NoArgsConstructor
                    @AllArgsConstructor
                    @Getter
                    public static class Station {
                        private int index; // Index of the station
                        private int stationID; // Station ID
                        private String stationName; // Station name
                        private double x; // X coordinate
                        private double y; // Y coordinate
                    }
                }
            }
        }
    }
}
