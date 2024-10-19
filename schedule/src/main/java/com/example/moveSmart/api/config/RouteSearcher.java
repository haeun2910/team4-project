package com.example.moveSmart.api.config;

import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.api.entity.RouteSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class RouteSearcher {
    private final RestTemplate routeSearchClient;
    @Value(("${odsay.uri}"))
    private String url;

    @Value("${odsay.key}")
    private String apiKey;

    public OdsayRouteSearchResponse searchAllRoutes(RouteSearchRequest requestDto) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("apiKey", apiKey)
                .queryParam("SX", requestDto.getStartLng())
                .queryParam("SY", requestDto.getStartLat())
                .queryParam("EX", requestDto.getEndLng())
                .queryParam("EY", requestDto.getEndLat())
                .queryParam("OPT", requestDto.getSortCriterion());
        URI uri = uriComponentsBuilder.build().toUri();
        log.info("Requesting Odsay API with URI: {}", uri.toString());
        ResponseEntity<OdsayRouteSearchResponse> response = routeSearchClient.getForEntity(
                uri,
                OdsayRouteSearchResponse.class
        );
        log.info("statusCode: {}", response.getStatusCode());
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("Failed to retrieve route data from Odsay API. Status code: " + response.getStatusCode());
        }
        return response.getBody();
    }

    public double calcRouteAverageTime(RouteSearchRequest requestDto) {
        OdsayRouteSearchResponse routeSearchResponseDto = this.searchAllRoutes(requestDto);
        return routeSearchResponseDto.getResult().getPath().stream()
                .mapToDouble(path -> path.getInfo().getTotalTime()) // Lấy tổng thời gian của mỗi tuyến
                .limit(5)// Giới hạn đến 5 tuyến đường đầu tiên
                .average()// Tính trung bình
                .orElse(0.0); // Trả về 0.0 nếu không có tuyến đường nào
    }
}
