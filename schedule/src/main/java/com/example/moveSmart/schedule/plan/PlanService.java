package com.example.moveSmart.schedule.plan;

import com.example.moveSmart.odsayApi.config.Client;
import com.example.moveSmart.odsayApi.entity.RemainingTimeInfoVo;
import com.example.moveSmart.odsayApi.entity.RouteSearchRequest;
import com.example.moveSmart.odsayApi.config.RouteSearcher;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

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

        Plan plan = Plan.builder()
                .title(planDto.getTitle())
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
        existingPlan.setTitle(plan.getTitle());
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
    public RemainingTimeInfoVo getTimeRemainingUntilRecentPlan() {
        // Lấy người dùng hiện tại từ authFacade
        UserEntity currentUser = authFacade.extractUser();
        log.info("Current User: {}", currentUser);

        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Tìm kế hoạch gần nhất của người dùng, kế hoạch phải có thời gian đến lớn hơn thời gian hiện tại
        Plan recentPlan = planRepo.findTopByUserAndArrivalAtGreaterThanOrderByArrivalAtAsc(currentUser, now)
                .orElseThrow();

        // Xây dựng đối tượng RouteSearchRequest từ thông tin của kế hoạch gần nhất
        RouteSearchRequest requestDto = RouteSearchRequest.builder()
                .startLng(recentPlan.getDepartureLng())
                .startLat(recentPlan.getDepartureLat())
                .endLng(recentPlan.getArrivalLng())
                .endLat(recentPlan.getArrivalLat())
                .sortCriterion(0) // 0 là giá trị mặc định của tiêu chí sắp xếp
                .build();

        // Tính toán thời gian trung bình của các tuyến đường
        int routeAverageMins = (int) Math.ceil(routeSearcher.calcRouteAverageTime(requestDto));

        // Tính toán tổng thời gian chuẩn bị từ các nhiệm vụ liên quan tới kế hoạch
        int preparationMins = Optional.ofNullable(recentPlan.getPlanTasks())
                .map(tasks -> tasks.stream()
                        .mapToInt(pt -> Optional.ofNullable(pt.getTask().getTime()).orElse(0)) // Thời gian của mỗi nhiệm vụ
                        .sum())
                .orElse(0); // Trả về 0 nếu danh sách nhiệm vụ rỗng hoặc null

        // Tính toán thời gian chuẩn bị
        LocalDateTime recentPlanArrivalAt = recentPlan.getArrivalAt();
        LocalDateTime preparationStartAt = recentPlanArrivalAt.minusMinutes(routeAverageMins + preparationMins);

        // Tính toán thời gian còn lại cho đến khi người dùng cần bắt đầu chuẩn bị
        Duration remainingTime = Duration.between(now, preparationStartAt);
        if (remainingTime.isNegative()) {
            remainingTime = Duration.ZERO; // Nếu thời gian còn lại là số âm, đặt thành 0
        }

        // Định nghĩa thời gian đệm (trong phút)
        int bufferTimeMins = 15; // Thay đổi giá trị này theo nhu cầu
        LocalDateTime recommendedDepartureTime = preparationStartAt.minusMinutes(bufferTimeMins);

        // Trả về đối tượng RemainingTimeInfoVo chứa thông tin về thời gian còn lại và thời gian khuyến nghị
        return new RemainingTimeInfoVo(remainingTime, routeAverageMins, preparationMins, recentPlanArrivalAt, recommendedDepartureTime);
    }



//    public String findRouteForPlan(Long planId) {
//        // Extract the currently authenticated user
//        UserEntity user = authFacade.extractUser();
//
//        // Fetch the Plan by ID, and ensure it belongs to the current user
//        Plan plan = planRepo.findByIdAndUser(planId, user)
//                .orElseThrow();
//
//        // Use OdsayClient to search for the route based on the Plan's coordinates
//        return odsayClient.searchRoute(plan);
//    }

}