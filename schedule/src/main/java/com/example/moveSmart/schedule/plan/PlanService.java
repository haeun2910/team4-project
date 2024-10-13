package com.example.moveSmart.schedule.plan;

import com.example.moveSmart.api.config.OdsayClient;
import com.example.moveSmart.route.RemainingTimeInfoVo;
import com.example.moveSmart.route.RouteSearchRequest;
import com.example.moveSmart.route.RouteSearcher;
import com.example.moveSmart.schedule.plan.dto.PlanDto;
import com.example.moveSmart.schedule.plan.dto.PlanTaskDto;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.entity.PlanTask;
import com.example.moveSmart.schedule.plan.repo.PlanRepo;
import com.example.moveSmart.schedule.plan.repo.PlanTaskRepo;
import com.example.moveSmart.schedule.task.dto.TaskDto;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.schedule.task.repo.TaskRepo;
import com.example.moveSmart.user.AuthenticationFacade;
import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;

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
    private final OdsayClient odsayClient;

    @Transactional
    public PlanDto createPlan(PlanDto planDto) {
        UserEntity user = authFacade.extractUser();
        UserEntity userId = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Plan plan = Plan.builder()
                .title(planDto.getTitle())
//                .startTime(planDto.getStartTime())
//                .endTime(planDto.getEndTime())
//                .startLocation(planDto.getStartLocation())
//                .destination(planDto.getDestination())
//                .mode(planDto.getTransportationMode())
//                .estimatedCost(planDto.getEstimatedCost())
                .departureName(planDto.getDepartureName())
                .departureLat(planDto.getDepartureLat())
                .departureLng(planDto.getDepartureLng())
                .arrivalName(planDto.getArrivalName())
                .arrivalAt(planDto.getArrivalAt())
                .arrivalLat(planDto.getArrivalLat())
                .arrivalLng(planDto.getArrivalLng())
                .notificationMessage(planDto.getNotificationMessage())
                .user(userId)
                .build();

        return PlanDto.fromEntity(planRepo.save(plan), true);
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
    public PlanDto updatePlan(PlanDto plan, Long planId) {
        UserEntity user = authFacade.extractUser();
        Plan existingPlan = planRepo.findById(planId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Check if the current user owns the plan
        if (!existingPlan.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this plan.");
        }

        // Update plan details
        existingPlan.setTitle(plan.getTitle());
//        existingPlan.setStartTime(plan.getStartTime());
//        existingPlan.setEndTime(plan.getEndTime());
//        existingPlan.setStartLocation(plan.getStartLocation());
//        existingPlan.setDestination(plan.getDestination());
//        existingPlan.setMode(plan.getTransportationMode());
//        existingPlan.setEstimatedCost(plan.getEstimatedCost());
        existingPlan.setDepartureName(plan.getDepartureName());
        existingPlan.setDepartureLat(plan.getDepartureLat());
        existingPlan.setDepartureLng(plan.getDepartureLng());
        existingPlan.setArrivalName(plan.getArrivalName());
        existingPlan.setArrivalLat(plan.getArrivalLat());
        existingPlan.setArrivalLng(plan.getArrivalLng());
        existingPlan.setNotificationMessage(plan.getNotificationMessage());


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

        Page<Plan> plans = planRepo.findByUser(user,pageable);
        return plans.map(plan -> PlanDto.fromEntity(plan, true));
    }
    public void completePlan(Long id) {
        UserEntity currentUser = authFacade.extractUser();
        LocalDateTime currentTime = LocalDateTime.now();

        Plan plan = planRepo.findById(id)
                .orElseThrow();

        if (!plan.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to complete this plan.");
        }
//        if (plan.getEndTime().isBefore(currentTime)) {
//            throw new IllegalArgumentException("End time must be in the future to complete the plan");
//        }

        plan.setCompleted(true);
        for (PlanTask task : plan.getPlanTasks()) {
            task.setCompleted(true);
        }
        planRepo.save(plan);

    }

//    public Page<PlanDto> getCompletedPlans(Pageable pageable) {
//        UserEntity user = authFacade.extractUser();
//        Page<Plan> plans = planRepo.findByCompletedTrueAndUserOrderByEndTimeAscStartTimeAsc(user, pageable);
//        return plans.map(PlanDto::fromEntity);
//    }
//
//    public Page<PlanDto> getExpiredPlans(Pageable pageable) {
//        UserEntity user = authFacade.extractUser();
//        LocalDateTime currentTime = LocalDateTime.now();
//        Page<Plan> plans = planRepo.findByCompletedFalseAndEndTimeLessThanAndUserOrderByEndTimeAscStartTimeAsc(currentTime, user, pageable);
//        return plans.map(PlanDto::fromEntity);
//    }

    @Transactional
    public RemainingTimeInfoVo getTimeRemainingUntilRecentPlan() {
        UserEntity currentUser = authFacade.extractUser();

        LocalDateTime now = LocalDateTime.now();
        Plan recentPlan = planRepo.findTopByUserAndArrivalAtGreaterThanOrderByArrivalAtAsc(currentUser, now)
                .orElseThrow();
        RouteSearchRequest requestDto = new RouteSearchRequest(
                recentPlan.getDepartureLng(),
                recentPlan.getDepartureLat(),
                recentPlan.getArrivalLng(),
                recentPlan.getArrivalLat(),
                0
        );
        int routeAverageMins = (int) Math.ceil(routeSearcher.calcRouteAverageTime(requestDto));
        int preparationMins = recentPlan.getPlanTasks().stream()
                .mapToInt(pt -> pt.getTask().getTime())
                .sum();
        LocalDateTime recentPlanArrivalAt = recentPlan.getArrivalAt();
        LocalDateTime preparationStartAt = recentPlanArrivalAt.minusMinutes(routeAverageMins + preparationMins);
        Duration remainingTime = Duration.between(now, preparationStartAt);
        return new RemainingTimeInfoVo(remainingTime, routeAverageMins, preparationMins, recentPlanArrivalAt);
    }

    public String findRouteForPlan(Long planId) {
        // Extract the currently authenticated user
        UserEntity user = authFacade.extractUser();

        // Fetch the Plan by ID, and ensure it belongs to the current user
        Plan plan = planRepo.findByIdAndUser(planId, user)
                .orElseThrow();

        // Use OdsayClient to search for the route based on the Plan's coordinates
        return odsayClient.searchRoute(plan);
    }

}