package com.example.schedule.schedule.repo;

import com.example.schedule.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

}
