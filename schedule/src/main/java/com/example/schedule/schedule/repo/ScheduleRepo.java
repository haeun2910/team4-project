package com.example.schedule.schedule.repo;

import com.example.schedule.schedule.entity.Schedule;
import com.example.schedule.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {
    Optional<Schedule> findByUser(UserEntity user);

}
