package com.example.moveSmart.api.controller;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.api.service.OdsayService;
import com.example.moveSmart.schedule.plan.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ApiController {
    private final Client client;
    private final PlanService planService;
    private final OdsayService odsayService;

    @GetMapping("/route/{planId}")
    public ResponseEntity<Map<String, List<OdsayRouteSearchResponse.Result.Path>>> getRoutesForPlan(
            @PathVariable Long planId) {
        Map<String, List<OdsayRouteSearchResponse.Result.Path>> routes = odsayService.findRoutesForPlan(planId);
        return ResponseEntity.ok(routes);
    }
    @GetMapping("/route/{planId}/transportation")
    public ResponseEntity<List<OdsayRouteSearchResponse.Result.Path>> getRoutesByTransportationType(
            @PathVariable Long planId,
            @RequestParam("type") int trafficType) { // Use an integer to represent the traffic type
        List<OdsayRouteSearchResponse.Result.Path> filteredPaths = odsayService.findRouteForPlan(planId, trafficType);
        return ResponseEntity.ok(filteredPaths);
    }
    @GetMapping("/search-location")
    public ResponseEntity<PlaceSearchResponse> searchLocation(@RequestParam String address) throws JsonProcessingException {
        log.info("Received address: {}", address);
        PlaceSearchResponse response = client.searchAddress(address);
        return ResponseEntity.ok(response);
    }

}
