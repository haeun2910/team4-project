package com.example.moveSmart.api.entity;

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
        private List<Path> path;

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        public static class Path {
            private int pathType;
            private Info info;
            private List<SubPath> subPath;

            @NoArgsConstructor
            @AllArgsConstructor
            @Getter
            public static class Info {
                private double trafficDistance;// Khoảng cách giao thông
                private int totalWalk; // Tổng thời gian đi bộ
                private int totalTime; // Tổng thời gian di chuyển
                private int payment; // Chi phí
                private int busTransitCount; // Số lần chuyển xe buýt
                private int subwayTransitCount;
                private int taxiTime;
                private double taxiDistance;
                private int personalCarTime;
                private double personalCarDistance;
                private String mapObj;
                private String firstStartStation;
                private String lastEndStation;
                private int totalStationCount;
                private int busStationCount;
                private int subwayStationCount;
                private double totalDistance;
                private int totalWalkTime;
            }

            @NoArgsConstructor
            @AllArgsConstructor
            @Getter
            public static class SubPath {
                private int trafficType;
                private int distance;
                private int sectionTime;
                private int stationCount;
                private List<Lane> lane;
                private String startName;
                private double startX;
                private double startY;
                private String endName;
                private double endX;
                private double endY;
                private String way;
                private int wayCode;
                private String door;
                private int startID;
                private int endID;
                private String startExitNo;
                private double startExitX;
                private double startExitY;
                private String endExitNo;
                private double endExitX;
                private double endExitY;
                private PassStopList passStopList;
                private int taxiSectionTime;
                private double taxiDistance;
                private int personalCarSectionTime;
                private double personalCarDistance;

                @NoArgsConstructor
                @AllArgsConstructor
                @Getter
                public static class Lane {
                    private String name;
                    private int subwayCode;
                    private int subwayCityCode;
                    private String busNo;
                    private int type;
                    private int busID;
                }

                @NoArgsConstructor
                @AllArgsConstructor
                @Getter
                public static class PassStopList {
                    private List<Station> stations;

                    @NoArgsConstructor
                    @AllArgsConstructor
                    @Getter
                    public static class Station {
                        private int index;
                        private int stationID;
                        private String stationName;
                        private String x;
                        private String y;
                        private String isNonStop;
                    }
                }
            }
        }
    }
}
