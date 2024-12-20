package com.example.moveSmart.schedule.plan;

import com.example.moveSmart.api.config.Client;
import com.example.moveSmart.api.entity.PlaceSearchResponse;
import com.example.moveSmart.api.entity.time.RemainingTimeInfoVo;
import com.example.moveSmart.api.entity.route.RouteSearchRequest;
import com.example.moveSmart.api.config.RouteSearcher;
import com.example.moveSmart.schedule.plan.dto.PlanDto;
import com.example.moveSmart.schedule.plan.dto.PlanTaskDto;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.entity.PlanTask;
import com.example.moveSmart.schedule.plan.repo.PlanRepo;
import com.example.moveSmart.schedule.plan.repo.PlanTaskRepo;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.schedule.task.repo.TaskRepo;
import com.example.moveSmart.user.AuthenticationFacade;
import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanService {
    private final AuthenticationFacade authFacade;
    private final PlanRepo planRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;
    private final PlanTaskRepo planTaskRepo;
    private final RouteSearcher routeSearcher;
    private final Client client;


    @Transactional
    public PlanDto createPlan(PlanDto planDto) {
        UserEntity user = authFacade.extractUser();
        UserEntity userId = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Get latitude and longitude for departure and arrival addresses
        PlaceSearchResponse departureLocation;
        PlaceSearchResponse arrivalLocation;

        // Determine if the departure name is specific or general
        if (isSpecificAddress(planDto.getDepartureName())) {
            departureLocation = client.searchAddress(planDto.getDepartureName());
        } else {
            departureLocation = client.searchPlace(planDto.getDepartureName());
        }

        // Determine if the arrival name is specific or general
        if (isSpecificAddress(planDto.getArrivalName())) {
            arrivalLocation = client.searchAddress(planDto.getArrivalName());
        } else {
            arrivalLocation = client.searchPlace(planDto.getArrivalName());
        }

        // Assuming the first result is the most relevant
        double departureLat = departureLocation.getPlaces().get(0).getLatitude();
        double departureLng = departureLocation.getPlaces().get(0).getLongitude();
        double arrivalLat = arrivalLocation.getPlaces().get(0).getLatitude();
        double arrivalLng = arrivalLocation.getPlaces().get(0).getLongitude();

        Plan plan = Plan.builder()
                .title(planDto.getTitle())
                .departureName(planDto.getDepartureName())
                .departureLat(departureLat) // set fetched latitude
                .departureLng(departureLng) // set fetched longitude
                .arrivalName(planDto.getArrivalName())
                .arrivalAt(planDto.getArrivalAt())
                .arrivalLat(arrivalLat) // set fetched latitude
                .arrivalLng(arrivalLng) // set fetched longitude
                .notificationMessage(planDto.getNotificationMessage())
                .user(userId)
                .build();

        return PlanDto.fromEntity(planRepo.save(plan), true);
    }

    // Helper method to determine if the address is specific
    private boolean isSpecificAddress(String address) {
        // Check for patterns indicating specific addresses (e.g., contains numbers and specific patterns)
        return address.matches(".*\\d.*") || address.matches(".*길.*\\d[-]\\d.*");
    }

    public PlanTaskDto createPlanTask(PlanTaskDto planTaskDto) {
        UserEntity user = authFacade.extractUser(); // Extract the authenticated user
        Long planId = planTaskDto.getPlanId();

        // Validate the provided Plan ID
        if (planId == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }

        // Retrieve the Plan entity based on the provided ID
        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found for ID: " + planId));

        // Create a new Task entity from the DTO
        Task task = Task.builder()
                .title(planTaskDto.getTitle())
                .time(planTaskDto.getTime())
                .plan(plan) // Associate the Plan with the Task
                .user(user)
                .build();

        // Save the Task entity and get the saved instance
        Task savedTask = taskRepo.save(task);

        // Create a new PlanTask entity linking the Plan and Task
        PlanTask planTask = PlanTask.builder()
                .task(savedTask) // Associate the saved Task
                .plan(plan) // Associate the Plan
                .user(user) // Associate the User if needed
                .build();

        // Save the PlanTask entity
        PlanTask savedPlanTask = planTaskRepo.save(planTask); // Ensure planTaskRepo is properly injected

        // Return the DTO representation of the saved PlanTask
        return PlanTaskDto.fromPlanTaskEntity(savedPlanTask, true);
    }


    @Transactional
    public PlanDto updatePlan(PlanDto planDto, Long planId) {
        UserEntity user = authFacade.extractUser();
        Plan existingPlan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Check if the current user owns the plan
        if (!existingPlan.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this plan.");
        }

        // Declare variables for departure and arrival locations
        PlaceSearchResponse departureLocation;
        PlaceSearchResponse arrivalLocation;

        // Determine if the departure name is specific or general
        if (isSpecificAddress(planDto.getDepartureName())) {
            departureLocation = client.searchAddress(planDto.getDepartureName());
        } else {
            departureLocation = client.searchPlace(planDto.getDepartureName());
        }

        // Determine if the arrival name is specific or general
        if (isSpecificAddress(planDto.getArrivalName())) {
            arrivalLocation = client.searchAddress(planDto.getArrivalName());
        } else {
            arrivalLocation = client.searchPlace(planDto.getArrivalName());
        }

        // Check if the API returned any results
        if (departureLocation.getPlaces().isEmpty() || arrivalLocation.getPlaces().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to find location for the provided addresses.");
        }

        // Assuming the first result is the most relevant
        double departureLat = departureLocation.getPlaces().get(0).getLatitude();
        double departureLng = departureLocation.getPlaces().get(0).getLongitude();
        double arrivalLat = arrivalLocation.getPlaces().get(0).getLatitude();
        double arrivalLng = arrivalLocation.getPlaces().get(0).getLongitude();

        // Update existing plan details
        existingPlan.setTitle(planDto.getTitle());
        existingPlan.setDepartureName(planDto.getDepartureName());
        existingPlan.setDepartureLat(departureLat); // Update with fetched latitude
        existingPlan.setDepartureLng(departureLng); // Update with fetched longitude
        existingPlan.setArrivalName(planDto.getArrivalName());
        existingPlan.setArrivalLat(arrivalLat); // Update with fetched latitude
        existingPlan.setArrivalLng(arrivalLng); // Update with fetched longitude
        existingPlan.setNotificationMessage(planDto.getNotificationMessage());
        existingPlan.setArrivalAt(planDto.getArrivalAt()); // If needed, adjust this line

        // Save the updated plan and return the DTO
        return PlanDto.fromEntity(planRepo.save(existingPlan), true);
    }

    @Transactional
    public void deletePlan(Long planId) {
        UserEntity user = authFacade.extractUser();
        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!plan.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        planRepo.deleteById(planId);
    }

    public Page<PlanDto> myPlan(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        LocalDateTime now = LocalDateTime.now();

        // Get future plans, sorted by arrival time ascending
        List<Plan> futurePlans = planRepo.findByUserAndArrivalAtAfter(user, now, Sort.by(Sort.Direction.ASC, "arrivalAt"));

        // Get past plans, sorted by arrival time descending (latest first)
        List<Plan> pastPlans = planRepo.findByUserAndArrivalAtBefore(user, now, Sort.by(Sort.Direction.DESC, "arrivalAt"));

        // Combine future plans and past plans into a single list
        List<Plan> allPlans = new ArrayList<>(futurePlans);
        allPlans.addAll(pastPlans);

        // Create a Page from the combined list
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPlans.size());
        Page<Plan> plans = new PageImpl<>(allPlans.subList(start, end), pageable, allPlans.size());

        // Map the Page<Plan> to Page<PlanDto>
        return plans.map(plan -> PlanDto.fromEntity(plan, true));
    }


    public PlanDto readOnePlan(Long planId) {
        UserEntity user = authFacade.extractUser();

        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan not found with id: " + planId));

        // Check if the plan belongs to the authenticated user
        if (!plan.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to view this plan.");
        }

        // Convert the Plan entity to PlanDto and return it
        return PlanDto.fromEntity(plan, true);
    }

    public void completePlan(Long id) {
        UserEntity currentUser = authFacade.extractUser();
        LocalDateTime currentTime = LocalDateTime.now();

        Plan plan = planRepo.findById(id)
                .orElseThrow();

        if (!plan.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to complete this plan.");
        }
        plan.setCompleted(true);
        for (PlanTask task : plan.getPlanTasks()) {
            task.setCompleted(true);
        }
        planRepo.save(plan);

    }

    public Page<PlanDto> getCompletedPlans(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        Page<Plan> plans = planRepo.findByUserAndCompletedTrue(user, pageable);
        return plans.map(PlanDto::fromEntity);
    }

    public Page<PlanDto> getIncompletePlans(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        Page<Plan> plans = planRepo.findByUserAndCompletedFalse(user,pageable);
        return plans.map(PlanDto::fromEntity);
    }

    @Transactional
    public RemainingTimeInfoVo getTimeRemainingUntilRecentPlan(Long planId, String transportType) {
        // Lấy người dùng hiện tại từ authFacade
        UserEntity currentUser = authFacade.extractUser();
        log.info("Current User: {}", currentUser);

        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Tìm kế hoạch theo id và thời gian đến phải lớn hơn thời gian hiện tại
        Plan recentPlan = planRepo.findByIdAndArrivalAtGreaterThan(planId, now)
                .orElseThrow(() -> new NoSuchElementException("No plan found with id " + planId + " and valid arrival time."));

        // Xây dựng đối tượng RouteSearchRequest từ thông tin của kế hoạch
        RouteSearchRequest requestDto = RouteSearchRequest.builder()
                .startLng(recentPlan.getDepartureLng())
                .startLat(recentPlan.getDepartureLat())
                .endLng(recentPlan.getArrivalLng())
                .endLat(recentPlan.getArrivalLat())
                .sortCriterion(0) // 0 là giá trị mặc định của tiêu chí sắp xếp
                .build();

        // Tính toán thời gian trung bình của các tuyến đường dựa trên loại phương tiện
        double routeAverageMins;
        if ("publicTransport".equalsIgnoreCase(transportType)) {
            routeAverageMins = Math.ceil(routeSearcher.calcRouteAverageTime(requestDto)); // Tính toán thời gian từ Odsay
        } else if ("carOrTaxi".equalsIgnoreCase(transportType)) {
            routeAverageMins = Math.ceil(routeSearcher.calcNCloudRouteAverageTime(requestDto)); // Tính toán thời gian từ NCloud
        } else {
            throw new IllegalArgumentException("Invalid transport type: " + transportType);
        }

        // Kiểm tra danh sách các nhiệm vụ
        Optional.ofNullable(recentPlan.getPlanTasks())
                .ifPresent(tasks -> tasks.forEach(task -> log.info("Task: {}, Time: {}", task.getTask().getTitle(), task.getTask().getTime())));

        // Tính toán tổng thời gian chuẩn bị từ các nhiệm vụ (Task) liên quan tới kế hoạch
        int totalReadyTimeAsMins = Optional.ofNullable(recentPlan.getPlanTasks())
                .map(tasks -> tasks.stream()
                        .filter(Objects::nonNull)
                        .mapToInt(pt -> Optional.ofNullable(pt.getTask().getTime()).orElse(0)) // Lấy thời gian của mỗi nhiệm vụ (nếu null trả về 0)
                        .sum())
                .orElse(0); // Trả về 0 nếu danh sách nhiệm vụ rỗng hoặc null

        log.info("Total Ready Time (in minutes): {}", totalReadyTimeAsMins);

        // Tính toán thời gian chuẩn bị
        LocalDateTime recentPlanArrivalAt = recentPlan.getArrivalAt();
        LocalDateTime preparationStartAt = recentPlanArrivalAt.minusMinutes((int) routeAverageMins + totalReadyTimeAsMins);

        // Tính toán thời gian còn lại cho đến khi người dùng cần đến (ArrivalAt)
        Duration remainingTime = Duration.between(now, recentPlanArrivalAt);
        if (remainingTime.isNegative()) {
            remainingTime = Duration.ZERO; // Nếu thời gian còn lại là số âm, đặt thành 0
        }


        // Định nghĩa thời gian đệm (trong phút)
        int bufferTimeMins = 15; // Thay đổi giá trị này theo nhu cầu
        LocalDateTime recommendedDepartureTime = preparationStartAt.minusMinutes(bufferTimeMins);

        // Trả về đối tượng RemainingTimeInfoVo chứa thông tin về thời gian còn lại và tổng thời gian chuẩn bị
        return new RemainingTimeInfoVo(remainingTime, (int) routeAverageMins, totalReadyTimeAsMins, recentPlanArrivalAt, recommendedDepartureTime);
    }

    public Page<PlanDto> findPlansByTitle(String title, Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        Long userId = user.getId(); // Assuming UserEntity has a method to get user ID

        // Fetch plans based on title and user ID
        List<Plan> plans = planRepo.findByTitleContainingIgnoreCaseAndUserId(title, userId);

        // Create a pageable response by converting the list to a page
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), plans.size());
        Page<Plan> page = new PageImpl<>(plans.subList(start, end), pageable, plans.size());

        return page.map(plan -> PlanDto.fromEntity(plan, true));
    }


}