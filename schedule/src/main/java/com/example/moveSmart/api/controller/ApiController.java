package com.example.moveSmart.api.controller;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.api.entity.RouteSearchResult;
import com.example.moveSmart.api.service.OdsayService;
import com.example.moveSmart.schedule.plan.PlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    private final RouteSearcher routeSearcher;


    @GetMapping("/route/{planId}")
    public ResponseEntity<RouteSearchResult> getRoutesForPlan(
            @PathVariable Long planId,
            @RequestParam(defaultValue = "0") int searchPathType) { // Default to '0' for all modes
        RouteSearchResult routes = odsayService.findRouteForPlan(planId, searchPathType);
        return ResponseEntity.ok(routes);
    }


    @GetMapping("/search-location")
    public ResponseEntity<PlaceSearchResponse> searchLocation(@RequestParam String address) throws JsonProcessingException {
        log.info("Received address: {}", address);
        PlaceSearchResponse response = client.searchAddress(address);
        return ResponseEntity.ok(response);
    }

}
