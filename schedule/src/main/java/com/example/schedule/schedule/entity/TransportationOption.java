package com.example.schedule.schedule.entity;

import com.example.schedule.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TransportationOption extends BaseEntity {
    private double estimatedCost;
    private double travelTime;
    @ManyToOne(fetch = FetchType.LAZY)
    private Route route;
    @Enumerated(EnumType.STRING)
    private TransportationMode mode;

}
