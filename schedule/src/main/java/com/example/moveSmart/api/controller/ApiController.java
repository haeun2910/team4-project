package com.example.moveSmart.api.controller;

import com.example.moveSmart.api.config.OdsayClient;
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

//    @GetMapping("/route")
//    public String searchRoute(){
//        //서율역
//        String SX ="126.9707979959352";
//        String SY = "37.5547020732267";
//        //잠실역
//        String EX = "127.10012275846414";
//        String EY = "37.513264531390575";
//
//        SearchRouteReq searchRouteReq = new SearchRouteReq(SX, SY, EX, EY);
//
//        return odsayClient.searchRoute(searchRouteReq);
//    }

//    @GetMapping("/{planId}/route")
//    public ResponseEntity<String> getRouteForPlan(@PathVariable Long planId) {
//        // Call the service method to find the route for the plan
//        String routeDetails = planService.findRouteForPlan(planId);
//
//        // Return the route details as the response
//        return ResponseEntity.ok(routeDetails);
//    }
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
