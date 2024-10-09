package com.example.schedule.schedule.repo;

import com.example.schedule.schedule.entity.Schedule;
import com.example.schedule.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUser(UserEntity user);
//    Page<Schedule> findAllByUser(UserEntity user, Pageable pageable);
    Page<Schedule> findAllByUserAndCompletedFalseAndEndTimeAfter(UserEntity user, LocalDateTime currentTime, Pageable pageable);
    Page<Schedule> findByCompletedTrueAndUserOrderByEndTimeAscStartTimeAsc(UserEntity user, Pageable pageable);
    Page<Schedule> findByCompletedFalseAndEndTimeLessThanAndUserOrderByEndTimeAscStartTimeAsc(LocalDateTime currentTime, UserEntity user, Pageable pageable);


}
