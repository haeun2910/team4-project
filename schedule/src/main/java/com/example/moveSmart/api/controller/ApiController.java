package com.example.moveSmart.api.controller;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.entity.route.NCloudRouteSearchResponse;
import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.route.RouteSearchResult;
import com.example.moveSmart.api.service.OdsayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ApiController {
    private final Client client;
    private final OdsayService odsayService;

    @GetMapping("/route/{planId}")
    public ResponseEntity<RouteSearchResult> getRoutesForPlan(@PathVariable Long planId) {
        RouteSearchResult routeSearchResult = odsayService.searchRouteByPlanIdWithPubTran(planId);
        return ResponseEntity.ok(routeSearchResult);
    }
    @GetMapping("/route-car-taxi/{planId}")
    public ResponseEntity<NCloudRouteSearchResponse> getRouteByPlanId(@PathVariable Long planId) {
        NCloudRouteSearchResponse response = odsayService.searchRouteByPlanIdWithPrivateCar(planId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search-location")
    public ResponseEntity<?> searchLocation(@RequestParam String address) {
        try {
            log.info("Received address: {}", address);
            PlaceSearchResponse response = client.searchAddress(address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching for location: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error.");
        }
    }

    @GetMapping("/search-place")
    public ResponseEntity<?> searchPlace(@RequestParam String address) {
        try {
            log.info("Received address: {}", address);
            PlaceSearchResponse response = client.searchPlace(address);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error searching for place: {}", address, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("error.");
        }
    }


}
