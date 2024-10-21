package com.example.moveSmart.api.service;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.api.entity.RouteSearchResult;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.repo.PlanRepo;
import com.example.moveSmart.user.AuthenticationFacade;
import com.example.moveSmart.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.moveSmart.api.entity.OdsayRouteSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OdsayService {
    private final AuthenticationFacade authFacade;
    private final Client client;
    private final PlanRepo planRepo;
    private final RouteSearcher routeSearcher;


    public RouteSearchResult findRouteForPlan(Long planId, int searchPathType) {
        // Extract the current user from the authentication facade
        UserEntity currentUser = authFacade.extractUser();

        // Retrieve the plan by ID, ensuring it belongs to the current user
        Plan plan = planRepo.findByIdAndUser(planId, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found for the current user"));

        // Use the OdsayClient to search for routes based on the plan's coordinates and the searchPathType
        OdsayRouteSearchResponse response = client.searchRoute(plan, searchPathType);
        log.info("ODSay API Response: {}", response);

        // Validate the response
        if (response == null || response.getResult() == null || response.getResult().getPath() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No routes found for the given plan");
        }

        // Return a RouteSearchResult object containing the search type and the list of routes
        int resultSearchType = response.getResult().getSearchType();
        List<OdsayRouteSearchResponse.Result.Path> paths = response.getResult().getPath();
        return new RouteSearchResult(resultSearchType, paths);
    }







}
