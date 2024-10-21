package com.example.moveSmart.api.entity;

import com.example.moveSmart.schedule.task.entity.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class RemainingTimeInfoVo {
    private final Duration remainingTime;
    private final int routeAverageTimeAsMins;
    private final int totalReadyTimeAsMins;
    private final LocalDateTime recentPlanArrivalAt;
    private final LocalDateTime recommendedDepartureTime;
}
