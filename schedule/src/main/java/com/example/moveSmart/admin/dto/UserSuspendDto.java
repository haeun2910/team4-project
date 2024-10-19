package com.example.moveSmart.admin.dto;


import com.example.moveSmart.user.entity.UserSuspend;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserSuspendDto {
    private Long id;
    private String username;
    private String suspendReason;
    private Boolean suspended;
    private LocalDateTime suspendStartDate;
    public static UserSuspendDto fromEntity(UserSuspend entity) {
        return UserSuspendDto.builder()
                .id(entity.getId())
                .username(entity.getTarget().getUsername())
                .suspendReason(entity.getSuspendReason())
                .suspended(entity.getSuspended())
                .suspendStartDate(entity.getSuspendStartDate())
                .build();
    }
}

