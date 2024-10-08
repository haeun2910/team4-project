package com.example.schedule.schedule;

import com.example.schedule.schedule.dto.ScheduleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    @PostMapping("create")
    public ScheduleDto create(@RequestBody ScheduleDto schedule) {return scheduleService.createSchedule(schedule);}

    @PutMapping("update")
    public ScheduleDto update(@RequestBody ScheduleDto schedule) {return scheduleService.updateSchedule(schedule);}
    @DeleteMapping("delete")
    public void delete(@RequestParam Long id) {scheduleService.deleteSchedule(id);}
}
