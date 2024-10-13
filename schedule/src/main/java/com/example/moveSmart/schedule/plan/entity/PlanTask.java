package com.example.moveSmart.schedule.plan.entity;

import com.example.moveSmart.entity.BaseEntity;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;



@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plantask_tb")
public class PlanTask extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "completed", nullable = false, columnDefinition = "boolean default false")
    private boolean completed;

}
