package com.example.schedule.admin.dto;


import com.example.schedule.user.entity.UserSuspend;
import lombok.*;

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
    public static UserSuspendDto fromEntity(UserSuspend entity) {
        return UserSuspendDto.builder()
                .id(entity.getId())
                .username(entity.getTarget().getUsername())
                .suspendReason(entity.getSuspendReason())
                .suspended(entity.getSuspended())
                .build();
    }
}

