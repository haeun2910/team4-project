package com.example.moveSmart.api.config;

import com.example.moveSmart.schedule.plan.entity.Plan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Component
public class OdsayClient {
    @Value("${odsay.key}")
    private String key;
    @Value(("${odsay.uri}"))
    private String routeSearchUri;
public String searchRoute(Plan plan) {
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
    var responseType = new ParameterizedTypeReference<String>() {};

    // Send the request to ODsay API
    var responseEntity = new RestTemplate().exchange(
            uri, HttpMethod.GET, httpEntity, responseType
    );

    return responseEntity.getBody();
}
}
