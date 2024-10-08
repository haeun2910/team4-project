package com.example.schedule.schedule;

import com.example.schedule.schedule.dto.ScheduleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("create")
    public ScheduleDto create(@RequestBody ScheduleDto schedule) {
        return scheduleService.createSchedule(schedule);
    }

    @PutMapping("update/{scheduleId}")
    public ScheduleDto update(@RequestBody ScheduleDto schedule, @PathVariable("scheduleId") Long scheduleId) {
        return scheduleService.updateSchedule(schedule);
    }
    @DeleteMapping("delete/{scheduleId}")
    public void delete(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
    }

    @GetMapping("my-schedules")
    public Page<ScheduleDto> mySchedules(Pageable pageable) {
        return scheduleService.mySchedule(pageable);
    }
    @PutMapping("/complete/{id}")
    public ResponseEntity<String> completeSchedule(@PathVariable Long id) {
        scheduleService.completeSchedule(id);
        return ResponseEntity.ok("Schedule marked as completed");
    }

    @GetMapping("/completed")
    public ResponseEntity<Page<ScheduleDto>> getCompletedSchedules(Pageable pageable) {
        Page<ScheduleDto> completedSchedules = scheduleService.getCompletedSchedules(pageable);
        return ResponseEntity.ok(completedSchedules);
    }

    @GetMapping("/expired")
    public ResponseEntity<Page<ScheduleDto>> getExpiredSchedules(Pageable pageable) {
        Page<ScheduleDto> expiredSchedules = scheduleService.getExpiredSchedules(pageable);
        return ResponseEntity.ok(expiredSchedules);
    }
}
