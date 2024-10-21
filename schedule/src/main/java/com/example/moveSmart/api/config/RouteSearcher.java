package com.example.moveSmart.api.config;

import com.example.moveSmart.api.entity.NCloudRouteSearchResponse;
import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.api.entity.RouteSearchRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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

    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.secret}")
    private String clientSecret;

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

    public NCloudRouteSearchResponse searchRouteWithPrivateCar(RouteSearchRequest requestDto) {
        // Xây dựng URI cho NCloud API
        URI uri = UriComponentsBuilder.fromUriString("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving")
                .queryParam("start", requestDto.getStartLng() + "," + requestDto.getStartLat())  // Tọa độ điểm bắt đầu
                .queryParam("goal", requestDto.getEndLng() + "," + requestDto.getEndLat())        // Tọa độ điểm đến
                .build().encode().toUri();

        log.info("[Request NCloud API] URI = {}", uri);

        // Tạo headers cho yêu cầu
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId); // ID của client
        headers.set("X-NCP-APIGW-API-KEY", clientSecret); // Secret của client

        // Gửi yêu cầu đến NCloud API
        ResponseEntity<NCloudRouteSearchResponse> responseEntity = routeSearchClient.exchange(
                uri, HttpMethod.GET, new HttpEntity<>(headers), NCloudRouteSearchResponse.class
        );

        // Kiểm tra và trả về phản hồi
        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
            throw new RuntimeException("Failed to retrieve route data from NCloud API. Status code: " + responseEntity.getStatusCode());
        }

        return responseEntity.getBody();
    }
    public double calcNCloudRouteAverageTime(RouteSearchRequest requestDto) {
        NCloudRouteSearchResponse response = searchRouteWithPrivateCar(requestDto);
        return response.getRoute().getTraoptimal().stream()
                .mapToDouble(optimal -> optimal.getSummary().getDurationInMinutes()) // Lấy tổng thời gian của mỗi tuyến
                .average() // Tính trung bình
                .orElse(0.0); // Trả về 0.0 nếu không có tuyến đường nào
    }
}
