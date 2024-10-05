package com.example.schedule.schedule.entity;

import com.example.schedule.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Route extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Location startLocation;
    @ManyToOne(fetch = FetchType.LAZY)
    private Location destination;
    // distance(km)
    private double distance;
    // estimated time(min)
    private double estimatedTime;
    @Builder.Default
    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private final List<TransportationOption> transportationOptions = new ArrayList<>();
}
