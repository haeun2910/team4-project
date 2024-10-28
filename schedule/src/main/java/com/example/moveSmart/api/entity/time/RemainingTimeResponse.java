package com.example.moveSmart.api.entity.time;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
public class RemainingTimeResponse {
    private final RemainingTime remainingTime;
    private final int routeAverageTimeAsMins;
    private final int totalReadyTimeAsMins;
    private final LocalDateTime recentPlanArrivalAt; // thời gian đến dự kiến
    private final LocalDateTime recommendedDepartureTime; // nên xuất phát trước mấy giờ

    @RequiredArgsConstructor
    @Getter
    public static class RemainingTime {
        private final long hours;
        private final int minutes;
    }

    public RemainingTimeResponse(RemainingTimeInfoVo remainingTimeInfoVo) {
        if (remainingTimeInfoVo == null || remainingTimeInfoVo.getRemainingTime() == null) {
            throw new IllegalArgumentException("RemainingTimeInfoVo or its RemainingTime cannot be null");
        }
        Duration remainingTime = remainingTimeInfoVo.getRemainingTime();
        long hours = Math.max(0, remainingTime.toDaysPart() * 24 + remainingTime.toHoursPart());
        int minutes = Math.max(0, remainingTime.toMinutesPart());
        this.remainingTime = new RemainingTime(hours, minutes);
        this.routeAverageTimeAsMins = remainingTimeInfoVo.getRouteAverageTimeAsMins();
        this.totalReadyTimeAsMins = remainingTimeInfoVo.getTotalReadyTimeAsMins();
        this.recentPlanArrivalAt = remainingTimeInfoVo.getRecentPlanArrivalAt();
        this.recommendedDepartureTime = remainingTimeInfoVo.getRecommendedDepartureTime();
    }
}