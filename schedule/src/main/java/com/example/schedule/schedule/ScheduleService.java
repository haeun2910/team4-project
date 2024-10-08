package com.example.schedule.schedule;

import com.example.schedule.schedule.dto.ScheduleDto;
import com.example.schedule.schedule.entity.Schedule;
import com.example.schedule.schedule.repo.ScheduleRepo;
import com.example.schedule.user.AuthenticationFacade;
import com.example.schedule.user.entity.UserEntity;
import com.example.schedule.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final AuthenticationFacade authFacade;
    private final ScheduleRepo scheduleRepo;
    private final UserRepo userRepo;

    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        UserEntity user = authFacade.extractUser();
        if (!userRepo.existsById(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Schedule schedule = Schedule.builder()
                .title(scheduleDto.getTitle())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .startLocation(scheduleDto.getStartLocation())
                .destination(scheduleDto.getDestination())
                .mode(scheduleDto.getTransportationMode())
                .estimatedCost(scheduleDto.getEstimatedCost())
                .build();
        return ScheduleDto.fromEntity(scheduleRepo.save(schedule));

    }

    public ScheduleDto updateSchedule(ScheduleDto schedule) {
        Schedule existingSchedule = scheduleRepo.findById(schedule.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existingSchedule.setTitle(schedule.getTitle())
                .setStartTime(schedule.getStartTime())
                .setEndTime(schedule.getEndTime())
                .setStartLocation(schedule.getStartLocation())
                .setDestination(schedule.getDestination())
                .setMode(schedule.getTransportationMode())
                .setEstimatedCost(schedule.getEstimatedCost());
        return ScheduleDto.fromEntity(scheduleRepo.save(existingSchedule));
    }
}
