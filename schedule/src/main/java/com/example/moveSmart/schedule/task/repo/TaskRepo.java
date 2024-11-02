package com.example.moveSmart.schedule.task.repo;

import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepo extends JpaRepository<Task, Long> {
    Optional<Task> findByUser(UserEntity user);
    Page<Task> findByUser(UserEntity user, Pageable pageable);
    Optional<Plan> findByPlanId(Long planId);
    Optional<Task> findByIdAndUser(Long taskId, UserEntity user);

}
