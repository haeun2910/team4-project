package com.example.moveSmart.schedule.plan;

import com.example.moveSmart.api.entity.time.RemainingTimeInfoVo;
import com.example.moveSmart.api.entity.time.RemainingTimeResponse;
import com.example.moveSmart.api.service.OdsayService;
import com.example.moveSmart.schedule.plan.dto.PlanDto;
import com.example.moveSmart.schedule.plan.dto.PlanTaskDto;
import com.example.moveSmart.user.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/plans")
@RequiredArgsConstructor
public class PlanController {
    private final PlanService planService;
    private final AuthenticationFacade authFacade;
    private final OdsayService odsayService;

    @PostMapping("create")
    public PlanDto create(@RequestBody PlanDto plan) {
        return planService.createPlan(plan);
    }

    @PostMapping("create-plan-task")
    public ResponseEntity<PlanTaskDto> createPlanTask(@RequestBody PlanTaskDto task) {
        // Validate the incoming task object
        if (task.getPlanId() == null) {
            return ResponseEntity.badRequest().body(null); // Or throw a custom exception
        }
        // Proceed to create the plan task
        PlanTaskDto createdTask = planService.createPlanTask(task);
        return ResponseEntity.ok(createdTask); // Return the created task with HTTP 200 status
    }

    @PutMapping("update/{planId}")
    public PlanDto update(@RequestBody PlanDto plan, @PathVariable("planId") Long planId) {

        return planService.updatePlan(plan, planId);
    }
    @DeleteMapping("delete/{planId}")
    public void delete(@PathVariable Long planId) {
        planService.deletePlan(planId);
    }

    @GetMapping("my-plans")
    public Page<PlanDto> myPlans(Pageable pageable) {
        return planService.myPlan(pageable);
    }

    @GetMapping("/{id}")
    public PlanDto getPlan(@PathVariable("id") Long planId) {
        return planService.readOnePlan(planId);
    }
    @PutMapping("/complete/{id}")
    public ResponseEntity<String> completePlan(@PathVariable Long id) {
        planService.completePlan(id);
        return ResponseEntity.ok("plan marked as completed");
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<PlanDto>> getCompletedplans(Pageable pageable) {
        Page<PlanDto> completedplans = planService.getCompletedPlans(pageable);
        return ResponseEntity.ok(completedplans);
    }

    @GetMapping("/incomplete")
    public ResponseEntity<Page<PlanDto>> getExpiredplans(Pageable pageable) {
        Page<PlanDto> expiredplans = planService.getIncompletePlans(pageable);
        return ResponseEntity.ok(expiredplans);
    }

    @GetMapping("/time-remaining/{planId}")
    public ResponseEntity<RemainingTimeResponse> getTimeRemaining(@PathVariable Long planId,
                                                                  @RequestParam String transportType) {
        RemainingTimeInfoVo timeRemaining = planService.getTimeRemainingUntilRecentPlan(planId, transportType);
        RemainingTimeResponse response = new RemainingTimeResponse(timeRemaining);
        return ResponseEntity.ok(response);
    }

}
