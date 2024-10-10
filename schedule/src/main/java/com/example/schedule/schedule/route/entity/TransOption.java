package com.example.schedule.schedule.route.entity;

import com.example.schedule.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransOption extends BaseEntity {
    private double estimatedCost;
    @ManyToOne(fetch = FetchType.LAZY)
    private Route route;
    @Enumerated(EnumType.STRING)
    private TransMode mode;

    public enum TransMode {
        PRIVATE_CAR,
        BIKE,
        WALK,
        TAXI,
        BUS,
        TRAIN

    }

}
