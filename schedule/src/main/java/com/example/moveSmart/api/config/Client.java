package com.example.moveSmart.api.config;

import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Client {
    @Value("${odsay.key}")
    private String key;
    @Value(("${odsay.uri}"))
    private String routeSearchUri;

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
        String url = UriComponentsBuilder.fromHttpUrl("https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode")
                .queryParam("query", query)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-ncp-apigw-api-key-id", clientId);
        headers.set("x-ncp-apigw-api-key", clientSecret);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
            JsonNode items = response.getBody() != null ? response.getBody().path("addresses") : null;

            List<PlaceSearchResponse.Place> places = new ArrayList<>();
            if (items != null && items.isArray() && !items.isEmpty()) {
                for (JsonNode item : items) {
                    String name = item.path("roadAddress").asText();
                    double latitude = item.path("y").asDouble();
                    double longitude = item.path("x").asDouble();
                    places.add(new PlaceSearchResponse.Place(name, latitude, longitude));
                }
            } else {
                log.error("No addresses found for query: {}", query);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No addresses found");
            }

            return new PlaceSearchResponse(places);
        } catch (Exception e) {
            log.error("Error occurred while searching for address: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while searching for the address");
        }
    }
}
