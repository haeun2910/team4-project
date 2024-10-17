package com.example.moveSmart.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlanArrivalRequest {
    private LocalDateTime actualArrivalAt;
}
