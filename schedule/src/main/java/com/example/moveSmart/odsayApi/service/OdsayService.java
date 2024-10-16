package com.example.moveSmart.odsayApi.service;

import com.example.moveSmart.odsayApi.config.Client;
import com.example.moveSmart.odsayApi.entity.PlaceSearchResponse;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.repo.PlanRepo;
import com.example.moveSmart.user.AuthenticationFacade;
import com.example.moveSmart.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.moveSmart.odsayApi.entity.OdsayRouteSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    public Map<String, List<OdsayRouteSearchResponse.Result.Path>> findRoutesForPlan(Long planId) {
        Map<String, List<OdsayRouteSearchResponse.Result.Path>> routes = new HashMap<>();

        // Tìm các loại đường
        for (int trafficType = 0; trafficType <= 4; trafficType++) {
            String routeType = getRouteType(trafficType);
            List<OdsayRouteSearchResponse.Result.Path> pathList = findRouteForPlan(planId, trafficType);
            routes.put(routeType, pathList);
        }

        return routes;
    }

    public List<OdsayRouteSearchResponse.Result.Path> findRouteForPlan(Long planId, int trafficType) {
        // Lấy người dùng hiện tại từ authFacade
        UserEntity user = authFacade.extractUser();

        // Fetch the Plan by ID and ensure it belongs to the current user
        Plan plan = planRepo.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found"));

        // Sử dụng OdsayClient để tìm kiếm tuyến đường dựa trên tọa độ của kế hoạch
        OdsayRouteSearchResponse response = client.searchRoute(plan);
        log.info("Response from ODSAY API: {}", response);
        log.info("Searching for paths with traffic type: {}", trafficType);
        // Kiểm tra nếu response hợp lệ
        if (response == null || response.getResult() == null || response.getResult().getPath() == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No routes found");
        }

        // Lọc và lấy các quãng đường dựa trên trafficType
        List<OdsayRouteSearchResponse.Result.Path> filteredPaths = response.getResult().getPath().stream()
                .filter(path -> path.getPathType() == trafficType)
                .collect(Collectors.toList());
        log.info("Filtered Paths for traffic type {}: {}", trafficType, filteredPaths.size());

        return filteredPaths;
    }

    private String getRouteType(int trafficType) {
        return switch (trafficType) {
            case 0 -> "walkingRoutes";
            case 1 -> "busRoutes";
            case 2 -> "subwayRoutes";
            case 3 -> "taxiRoutes";
            case 4 -> "carRoutes";
            default -> "unknownRoutes";
        };
    }
}
