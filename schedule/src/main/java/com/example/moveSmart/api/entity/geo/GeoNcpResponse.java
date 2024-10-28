package com.example.moveSmart.api.entity.geo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GeoNcpResponse {
    private String status;
    private GeoMeta meta;
    private List<GeoAddress> addresses;
    private String errorMessage;
}
