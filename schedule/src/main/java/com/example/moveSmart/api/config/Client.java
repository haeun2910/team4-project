package com.example.moveSmart.api.config;

import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.api.entity.GeoAddress;
import com.example.moveSmart.api.entity.GeoNcpResponse;
import com.example.moveSmart.api.repo.NcpMapApiService;
import com.example.moveSmart.schedule.plan.entity.Plan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class Client {
    @Value("${odsay.key}")
    private String key;
    @Value(("${odsay.uri}"))
    private String routeSearchUri;

    private final NcpMapApiService apiService;


    @Value("${naver.client-id}")
    private String clientId;

    @Value("${naver.secret}")
    private String clientSecret;

    @Value("${kakao.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;

    public OdsayRouteSearchResponse searchRoute(Plan plan) {
        // Build the URI using the coordinates from the Plan object
        URI uri = UriComponentsBuilder.fromUriString(routeSearchUri)
                .queryParam("SX", plan.getDepartureLng())  // SX: Departure Longitude
                .queryParam("SY", plan.getDepartureLat())  // SY: Departure Latitude
                .queryParam("EX", plan.getArrivalLng())    // EX: Arrival Longitude
                .queryParam("EY", plan.getArrivalLat())    // EY: Arrival Latitude
                .queryParam("apiKey", key)
                .build().encode().toUri();

        log.info("[request api] uri = {}", uri);

        // HttpEntity for request
        var httpEntity = new HttpEntity<>(new HttpHeaders());
        var responseType = new ParameterizedTypeReference<OdsayRouteSearchResponse>() {};

        // Send the request to ODsay API
        var responseEntity = new RestTemplate().exchange(
                uri, HttpMethod.GET, httpEntity, responseType
        );

        return responseEntity.getBody();
    }

    public PlaceSearchResponse searchAddress(String query) {
        GeoNcpResponse response = apiService.geocode(Map.of("query", query));
        log.info(response.toString());

        List<PlaceSearchResponse.Place> places = new ArrayList<>();
        // Validate the response
        if (response != null && response.getAddresses() != null && !response.getAddresses().isEmpty()) {
            for (GeoAddress address : response.getAddresses()) {
                String name = address.getRoadAddress(); // Get the road address
                double latitude = Double.parseDouble(address.getY()); // Get latitude (ensure it's parsed as a double)
                double longitude = Double.parseDouble(address.getX()); // Get longitude (ensure it's parsed as a double)

                // Create a new Place object and add it to the list
                places.add(new PlaceSearchResponse.Place(name, latitude, longitude));
            }
        } else {
            log.warn("No addresses found for query: {}", query);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No addresses found");
        }

        return new PlaceSearchResponse(places);
    }
}
