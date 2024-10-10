package com.example.moveSmart.schedule.plan;

import com.example.moveSmart.schedule.plan.dto.PlanDto;
import com.example.moveSmart.schedule.plan.dto.PlanTaskDto;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.plan.repo.PlanRepo;
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

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PlanService {
    private final AuthenticationFacade authFacade;
    private final PlanRepo planRepo;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    @Transactional
    public PlanDto createPlan(PlanDto planDto) {
        UserEntity user = authFacade.extractUser();
        UserEntity userId = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Plan plan = Plan.builder()
                .title(planDto.getTitle())
                .startTime(planDto.getStartTime())
                .endTime(planDto.getEndTime())
                .startLocation(planDto.getStartLocation())
                .destination(planDto.getDestination())
                .mode(planDto.getTransportationMode())
                .estimatedCost(planDto.getEstimatedCost())
                .user(userId)
                .build();

        return PlanDto.fromEntity(planRepo.save(plan), true);
    }
    public PlanTaskDto createPlanTask(PlanTaskDto planTaskDto) {
        UserEntity user = authFacade.extractUser();

        Long planId = planTaskDto.getPlanId();
        if (planId == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }

        Plan plan = planRepo.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found for ID: " + planId));

        Task task = Task.builder()
                .title(planTaskDto.getTitle())
                .user(user)
                .plan(plan)
                .build();
         return PlanTaskDto.fromPlanTaskEntity(taskRepo.save(task), true);

    }

    @Transactional
    public PlanDto updatePlan(PlanDto plan) {
        UserEntity user = authFacade.extractUser();
        Plan existingPlan = planRepo.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existingPlan.setTitle(plan.getTitle());
        existingPlan.setStartTime(plan.getStartTime());
        existingPlan.setEndTime(plan.getEndTime());
        existingPlan.setStartLocation(plan.getStartLocation());
        existingPlan.setDestination(plan.getDestination());
        existingPlan.setMode(plan.getTransportationMode());
        existingPlan.setEstimatedCost(plan.getEstimatedCost());
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
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable sortedByEndAndStartTime = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.asc("endTime"), Sort.Order.asc("startTime"))
        );

        Page<Plan> plan = planRepo.findAllByUserAndCompletedFalseAndEndTimeAfter(user, currentTime, sortedByEndAndStartTime);
        return plan.map(PlanDto::fromEntity);
    }
    public void completePlan(Long id) {
        UserEntity currentUser = authFacade.extractUser();
        LocalDateTime currentTime = LocalDateTime.now();

        Plan plan = planRepo.findById(id)
                .orElseThrow();

        if (!plan.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to complete this plan.");
        }
        if (plan.getEndTime().isBefore(currentTime)) {
            throw new IllegalArgumentException("End time must be in the future to complete the plan");
        }

        plan.setCompleted(true);
        planRepo.save(plan);
    }

    public Page<PlanDto> getCompletedPlans(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        Page<Plan> plans = planRepo.findByCompletedTrueAndUserOrderByEndTimeAscStartTimeAsc(user, pageable);
        return plans.map(PlanDto::fromEntity);
    }

    public Page<PlanDto> getExpiredPlans(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        LocalDateTime currentTime = LocalDateTime.now();
        Page<Plan> plans = planRepo.findByCompletedFalseAndEndTimeLessThanAndUserOrderByEndTimeAscStartTimeAsc(currentTime, user, pageable);
        return plans.map(PlanDto::fromEntity);
    }
}