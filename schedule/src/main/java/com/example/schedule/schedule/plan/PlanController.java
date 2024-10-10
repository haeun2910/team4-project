package com.example.schedule.schedule.plan;

import com.example.schedule.schedule.plan.dto.PlanDto;
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

    @PostMapping("create")
    public PlanDto create(@RequestBody PlanDto plan) {
        return planService.createPlan(plan);
    }

    @PutMapping("update/{planId}")
    public PlanDto update(@RequestBody PlanDto plan, @PathVariable("planId") Long planId) {
        return planService.updatePlan(plan);
    }
    @DeleteMapping("delete/{planId}")
    public void delete(@PathVariable Long planId) {
        planService.deletePlan(planId);
    }

    @GetMapping("my-plans")
    public Page<PlanDto> myPlans(Pageable pageable) {
        return planService.myPlan(pageable);
    }
    @PutMapping("/complete/{id}")
    public ResponseEntity<String> completeplan(@PathVariable Long id) {
        planService.completePlan(id);
        return ResponseEntity.ok("plan marked as completed");
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<PlanDto>> getCompletedplans(Pageable pageable) {
        Page<PlanDto> completedplans = planService.getCompletedPlans(pageable);
        return ResponseEntity.ok(completedplans);
    }

    @GetMapping("/expired")
    public ResponseEntity<Page<PlanDto>> getExpiredplans(Pageable pageable) {
        Page<PlanDto> expiredplans = planService.getExpiredPlans(pageable);
        return ResponseEntity.ok(expiredplans);
    }
}
