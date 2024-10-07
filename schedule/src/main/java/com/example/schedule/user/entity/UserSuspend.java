package com.example.schedule.user.entity;

import com.example.schedule.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role_suspend_req")
public class UserSuspend extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity target;
    private String suspendReason;
    @Setter
    private Boolean suspended;

}