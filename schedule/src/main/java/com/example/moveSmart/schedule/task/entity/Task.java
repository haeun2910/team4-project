package com.example.moveSmart.schedule.task.entity;

import com.example.moveSmart.entity.BaseEntity;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name ="task_table")
public class Task extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "plan_id")
    private Plan plan;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
    @Setter
    private String title;
    @Setter
    private Integer time;
    @Setter
    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private boolean completed;
}
