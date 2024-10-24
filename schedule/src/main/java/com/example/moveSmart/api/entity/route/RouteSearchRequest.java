package com.example.moveSmart.api.entity.route;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteSearchRequest {
    private double startLng; // Kinh độ điểm xuất phát
    private double startLat; // Vĩ độ điểm xuất phát
    private double endLng;   // Kinh độ điểm đến
    private double endLat;   // Vĩ độ điểm đến
    private int sortCriterion; // Tiêu chí sắp xếp tuyến đường

    // Phương thức chuyển đổi từ đối tượng khác sang RouteSearchRequest (nếu cần)
    public static RouteSearchRequest fromCoordinates(double startLng, double startLat, double endLng, double endLat, int sortCriterion) {
        return RouteSearchRequest.builder()
                .startLng(startLng)
                .startLat(startLat)
                .endLng(endLng)
                .endLat(endLat)
                .sortCriterion(sortCriterion)
                .build();
    }
}
