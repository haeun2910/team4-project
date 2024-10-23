package com.example.moveSmart.api.entity.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NaverSearchResponse {
    private String lastBuildDate; // Thời gian xây dựng cuối cùng
    private int total; // Tổng số kết quả
    private int start; // Bắt đầu từ
    private int display; // Số lượng hiển thị
    private List<NaverSearchItem> items; // Danh sách các mục tìm thấy
}