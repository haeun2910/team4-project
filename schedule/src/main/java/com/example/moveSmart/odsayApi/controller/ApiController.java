package com.example.moveSmart.odsayApi.controller;

import com.example.moveSmart.odsayApi.config.OdsayClient;
import com.example.moveSmart.schedule.plan.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ApiController {
    private final OdsayClient odsayClient;
    private final PlanService planService;

    @GetMapping("/route/{planId}")
    public ResponseEntity<Map<String, Object>> getRouteForPlan(@PathVariable Long planId) {
        // Fetch the route coordinates
        String routeDetails = planService.findRouteForPlan(planId);

        // For this example, assume routeDetails contains start and end coordinates
        Map<String, Object> response = new HashMap<>();
        response.put("route", routeDetails); // Assuming the route details contain all necessary points

        return ResponseEntity.ok(response);
    }
}
