package com.example.schedule.user.entity;

import com.example.schedule.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

}