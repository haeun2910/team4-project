package com.example.moveSmart.schedule.plan.repo;


import com.example.moveSmart.schedule.plan.entity.PlanTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanTaskRepo extends JpaRepository<PlanTask, Long> {
    Long deleteByPlanId(Long planId);
    List<PlanTask> findByPlanId(Long planId);
}
