package com.example.moveSmart.api.controller;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.entity.route.NCloudRouteSearchResponse;
import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.route.OdsayLaneInfoResponse;
import com.example.moveSmart.api.entity.route.OdsayRouteSearchResponse;
import com.example.moveSmart.api.entity.route.RouteSearchResult;
import com.example.moveSmart.api.service.OdsayService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class ApiController {
    private final Client client;
    private final OdsayService odsayService;

    @GetMapping("/route/{planId}")
    public ResponseEntity<OdsayRouteSearchResponse> getRoutesForPlan(@PathVariable Long planId) {
        OdsayRouteSearchResponse response = odsayService.searchRouteByPlanIdWithPubTran(planId);
        return ResponseEntity.ok(response);
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

    @GetMapping("/lane-info/{planId}")
    public ResponseEntity<OdsayLaneInfoResponse> getLaneInfoByPlanId(@PathVariable String planId) {
        try {
            // Trim and convert the planId from String to Long
            Long trimmedPlanId = Long.valueOf(planId.trim());

            // Load lane info based on the plan ID
            OdsayLaneInfoResponse response = odsayService.loadLaneInfoByPlanId(trimmedPlanId);

            // Return the response with HTTP 200 OK status
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            // Handle case where planId is not a valid Long
            log.error("Invalid plan ID format: {}", planId, e);
            return ResponseEntity.badRequest().build(); // Return 400 Bad Request
        } catch (ResponseStatusException e) {
            // Handle custom response status exceptions thrown by the service
            log.error("Error fetching lane info for plan ID {}: {}", planId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).build(); // Return the appropriate status
        } catch (Exception e) {
            // Handle any other unexpected exceptions
            log.error("Unexpected error occurred while fetching lane info for plan ID {}: {}", planId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Return 500 Internal Server Error
        }
    }

}
