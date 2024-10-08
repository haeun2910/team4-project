package com.example.schedule.schedule;

import com.example.schedule.schedule.dto.ScheduleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
}
