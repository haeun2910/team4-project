package com.example.moveSmart.api.controller;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.api.entity.NCloudRouteSearchResponse;
import com.example.moveSmart.api.entity.geo.PlaceSearchResponse;
import com.example.moveSmart.api.entity.RouteSearchResult;
import com.example.moveSmart.api.service.OdsayService;
import com.example.moveSmart.schedule.plan.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ApiController {
    private final Client client;
    private final PlanService planService;
    private final OdsayService odsayService;
    private final RouteSearcher routeSearcher;


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
    public ResponseEntity<PlaceSearchResponse> searchLocation(@RequestParam String address) throws JsonProcessingException {
        log.info("Received address: {}", address);
        PlaceSearchResponse response = client.searchAddress(address);
        return ResponseEntity.ok(response);
    }

}
