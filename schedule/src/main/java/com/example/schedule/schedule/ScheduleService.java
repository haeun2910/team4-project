package com.example.schedule.schedule;

import com.example.schedule.schedule.dto.ScheduleDto;
import com.example.schedule.schedule.entity.Schedule;
import com.example.schedule.schedule.repo.ScheduleRepo;
import com.example.schedule.user.AuthenticationFacade;
import com.example.schedule.user.entity.UserEntity;
import com.example.schedule.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleService {
    private final AuthenticationFacade authFacade;
    private final ScheduleRepo scheduleRepo;
    private final UserRepo userRepo;

    @Transactional
    public ScheduleDto createSchedule(ScheduleDto scheduleDto) {
        UserEntity user = authFacade.extractUser();
        UserEntity userId = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Schedule schedule = Schedule.builder()
                .title(scheduleDto.getTitle())
                .startTime(scheduleDto.getStartTime())
                .endTime(scheduleDto.getEndTime())
                .startLocation(scheduleDto.getStartLocation())
                .destination(scheduleDto.getDestination())
                .mode(scheduleDto.getTransportationMode())
                .estimatedCost(scheduleDto.getEstimatedCost())
                .user(userId)
                .build();

        return ScheduleDto.fromEntity(scheduleRepo.save(schedule), true);


    }

    @Transactional
    public ScheduleDto updateSchedule(ScheduleDto schedule) {
        UserEntity user = authFacade.extractUser();
        Schedule existingSchedule = scheduleRepo.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        existingSchedule.setTitle(schedule.getTitle());
        existingSchedule.setStartTime(schedule.getStartTime());
        existingSchedule.setEndTime(schedule.getEndTime());
        existingSchedule.setStartLocation(schedule.getStartLocation());
        existingSchedule.setDestination(schedule.getDestination());
        existingSchedule.setMode(schedule.getTransportationMode());
        existingSchedule.setEstimatedCost(schedule.getEstimatedCost());
        return ScheduleDto.fromEntity(scheduleRepo.save(existingSchedule), true);

    }
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        UserEntity user = authFacade.extractUser();
        Schedule schedule = scheduleRepo.findById(scheduleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!schedule.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        scheduleRepo.deleteById(scheduleId);
    }

    public Page<ScheduleDto> mySchedule(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        Pageable sortedByEndAndStartTime = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.asc("endTime"), Sort.Order.asc("startTime"))
        );

        Page<Schedule> schedule = scheduleRepo.findAllByUser(user, sortedByEndAndStartTime);
        return schedule.map(ScheduleDto::fromEntity);
    }

}