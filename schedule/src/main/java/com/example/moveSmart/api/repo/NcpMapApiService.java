package com.example.moveSmart.api.repo;

import com.example.moveSmart.api.entity.geo.GeoNcpResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

public interface NcpMapApiService {

    // geocode
    @GetExchange("/map-geocode/v2/geocode")
    GeoNcpResponse geocode(
            @RequestParam
            Map<String, Object> params
    );

}
