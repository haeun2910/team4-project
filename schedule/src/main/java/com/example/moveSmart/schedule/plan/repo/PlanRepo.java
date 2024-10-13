package com.example.moveSmart.schedule.plan.repo;

import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PlanRepo extends JpaRepository<Plan, Long> {
    Page<Plan> findByUser(UserEntity user, Pageable pageable);
//    Page<Plan> findAllByUserAndCompletedFalseAndEndTimeAfter(UserEntity user, LocalDateTime currentTime, Pageable pageable);
//    Page<Plan> findByCompletedTrueAndUserOrderByEndTimeAscStartTimeAsc(UserEntity user, Pageable pageable);
//    Page<Plan> findByCompletedFalseAndEndTimeLessThanAndUserOrderByEndTimeAscStartTimeAsc(LocalDateTime currentTime, UserEntity user, Pageable pageable);
    Optional<Plan> findTopByUserAndArrivalAtGreaterThanOrderByArrivalAtAsc (UserEntity user, LocalDateTime now);
    Optional<Plan> findByIdAndUser(Long planId, UserEntity user);




}
