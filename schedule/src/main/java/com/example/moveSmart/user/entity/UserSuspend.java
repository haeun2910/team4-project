package com.example.moveSmart.user.entity;

import com.example.moveSmart.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_suspend_req")
public class UserSuspend extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    private UserEntity target;
    @Setter
    private String suspendReason;
    @Setter
    private Boolean suspended;
    @Setter
    private LocalDateTime suspendStartDate;

}