package com.example.moveSmart.odsayApi.controller;

import com.example.moveSmart.odsayApi.config.OdsayClient;
import com.example.moveSmart.odsayApi.entity.OdsayRouteSearchResponse;
import com.example.moveSmart.odsayApi.service.OdsayService;
import com.example.moveSmart.schedule.plan.PlanService;
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
    private final OdsayClient odsayClient;
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
}
