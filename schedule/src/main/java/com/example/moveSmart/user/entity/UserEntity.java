package com.example.moveSmart.user.entity;

import com.example.moveSmart.entity.BaseEntity;
import com.example.moveSmart.schedule.plan.entity.Plan;
import com.example.moveSmart.schedule.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class UserEntity extends BaseEntity {
    @Setter
    @Column(unique = true)
    private String username;
    @Setter
    private String password;
    @Setter
    private String name;
    @Setter
    private Integer age;
    @Setter
    @Column(unique = true)
    private String email;
    @Setter
    @Column(unique = true)
    private String phone;
    @Setter
    private String profileImg;
    @Setter
    private String suspendReason;
    @Setter
    private LocalDateTime suspendStartDate;
    @Setter
    @Builder.Default
    private String roles = "ROLE_INACTIVE";
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private final List<Plan> plans = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private final List<Task> tasks = new ArrayList<>();

}
