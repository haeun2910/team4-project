package com.example.moveSmart.api.service;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.api.entity.route.NCloudRouteSearchResponse;
import com.example.moveSmart.api.entity.route.OdsayLaneInfoResponse;
import com.example.moveSmart.api.entity.route.RouteSearchResult;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.repo.PlanRepo;
import com.example.moveSmart.user.AuthenticationFacade;
import com.example.moveSmart.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.moveSmart.api.entity.route.OdsayRouteSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OdsayService {
    private final AuthenticationFacade authFacade;
    private final Client client;
    private final PlanRepo planRepo;
    private final RouteSearcher routeSearcher;

    public OdsayRouteSearchResponse searchRouteByPlanIdWithPubTran(Long planId) {
        // Retrieve the current user from AuthFacade
        UserEntity currentUser = authFacade.extractUser();

        // Find the plan by ID, ensuring it belongs to the current user
        Plan plan = planRepo.findByIdAndUser(planId, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found for the current user"));

        // Use the Client to search for routes based on the Plan
        return client.searchRouteWithPubTran(plan);
        
    }

    public NCloudRouteSearchResponse searchRouteByPlanIdWithPrivateCar(Long planId) {
        // Find the plan by ID
        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        // Call the method to search route without public transport
        return client.searchRouteWithPrivateCar(plan);
    }

    public OdsayLaneInfoResponse loadLaneInfoByPlanId(Long planId) {
        UserEntity currentUser = authFacade.extractUser();

        // Find the plan by ID, ensuring it belongs to the current user
        Plan plan = planRepo.findByIdAndUser(planId, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found for the current user"));

        // Retrieve the route response based on the plan
        OdsayRouteSearchResponse routeResponse = client.searchRouteWithPubTran(plan);

        // Extract mapObject from the route response
        String mapObject = extractMapObjectFromResponse(routeResponse);

        // Load lane information based on the extracted mapObject
        OdsayLaneInfoResponse laneInfoResponse = client.loadLaneInfo(mapObject);

        // Handle or process the laneInfoResponse if necessary
        return laneInfoResponse;
    }

    public String extractMapObjectFromResponse(OdsayRouteSearchResponse routeResponse) {
        // Check if the result is not null and has paths
        if (routeResponse != null && routeResponse.getResult() != null) {
            List<OdsayRouteSearchResponse.Result.Path> paths = routeResponse.getResult().getPath();

            if (paths != null && !paths.isEmpty()) {
                // Get the first path from the list
                OdsayRouteSearchResponse.Result.Path firstPath = paths.get(0);
                OdsayRouteSearchResponse.Result.Path.Info info = firstPath.getInfo();

                // Check if info is not null and return mapObj
                if (info != null) {
                    return info.getMapObj(); // Extracting mapObj
                }
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "mapObj not found in the response");
    }





}
