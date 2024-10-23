package com.example.moveSmart.api.entity.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverSearchItem {
    private String title; // Tiêu đề
    private String link; // Liên kết
    private String category; // Danh mục
    private String description; // Mô tả
    private String telephone; // Số điện thoại
    private String address; // Địa chỉ
    private String roadAddress; // Địa chỉ đường
    private String mapx; // Kinh độ
    private String mapy; // Vĩ độ
    // Phương thức để lấy kinh độ dưới dạng double
    public double getLongitude() {
        return Double.parseDouble(mapx) / 10000000.0; // Chia cho 10.000.000
    }

    // Phương thức để lấy vĩ độ dưới dạng double
    public double getLatitude() {
        return Double.parseDouble(mapy) / 10000000.0; // Chia cho 10.000.000
    }
}
