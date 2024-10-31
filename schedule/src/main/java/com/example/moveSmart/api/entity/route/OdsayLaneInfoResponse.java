package com.example.moveSmart.api.entity.route;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OdsayLaneInfoResponse {
    private Result result;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class Result {
        private List<Lane> lane;

        @NoArgsConstructor
        @AllArgsConstructor
        @Getter
        public static class Lane {
            private int laneClass; // Changed from 'class' to 'laneClass' since 'class' is a reserved keyword
            private int type;
            private List<Section> section;

            @NoArgsConstructor
            @AllArgsConstructor
            @Getter
            public static class Section {
                private List<GraphPosition> graphPos;

                @NoArgsConstructor
                @AllArgsConstructor
                @Getter
                public static class GraphPosition {
                    private double x;
                    private double y;
                }
            }
        }
    }
}
