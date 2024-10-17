package com.example.moveSmart.api.entity;

import lombok.Data;

import java.util.List;

@Data
public class GeoAddress {
    private String roadAddress;
    private String jibunAddress;
    private String englishAddress;
    private List<Object> addressElements;
    private String x;
    private String y;
    private Double distance;
}
