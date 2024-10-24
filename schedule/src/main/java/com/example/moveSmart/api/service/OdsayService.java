package com.example.moveSmart.api.service;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.api.entity.route.NCloudRouteSearchResponse;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class OdsayService {
    private final AuthenticationFacade authFacade;
    private final Client client;
    private final PlanRepo planRepo;
    private final RouteSearcher routeSearcher;


    public RouteSearchResult searchRouteByPlanIdWithPubTran(Long planId) {
        // Retrieve the current user from AuthFacade
        UserEntity currentUser = authFacade.extractUser();

        // Find the plan by ID, ensuring it belongs to the current user
        Plan plan = planRepo.findByIdAndUser(planId, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found for the current user"));

        // Use the Client to search for routes based on the Plan
        OdsayRouteSearchResponse response = client.searchRouteWithPubTran(plan);
        log.info("ODSay API Response: {}", response);

        // Check if the response is valid
        if (response == null || response.getResult() == null || response.getResult().getPath() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No routes found for the given plan");
        }

        // Return the RouteSearchResult with searchType and list of paths
        return new RouteSearchResult(response.getResult().getSearchType(), response.getResult().getPath());
    }

    public NCloudRouteSearchResponse searchRouteByPlanIdWithPrivateCar(Long planId) {
        // Find the plan by ID
        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        // Call the method to search route without public transport
        return client.searchRouteWithPrivateCar(plan);
    }
}
