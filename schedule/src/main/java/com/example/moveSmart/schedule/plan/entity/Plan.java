package com.example.moveSmart.schedule.plan.entity;


import com.example.moveSmart.entity.BaseEntity;
import com.example.moveSmart.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="plan_table")
public class Plan extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Setter
    private String title;
    @Setter
//    @Column(nullable = false)
    private String departureName;
    @Setter
//    @Column(nullable = false)
    private Double departureLat;
    @Setter
//    @Column(nullable = false)
    private Double departureLng;
    @Setter
//    @Column(nullable = false)
    private String arrivalName;
    @Setter
//    @Column(nullable = false)
    private Double arrivalLat;
    @Setter
//    @Column(nullable = false)
    private Double arrivalLng;
    @Setter
//    @Column(nullable = false)
    private LocalDateTime arrivalAt;
    @Setter
//    @Column
    private String notificationMessage;
    @Setter
//    @Column
    private LocalDateTime actualArrivalAt;
    @Setter
    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private boolean completed;
    @OneToMany(mappedBy = "plan")
    private final List<PlanTask> planTasks = new ArrayList<>();

}
