package com.example.moveSmart.user.dto;

import com.example.moveSmart.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private Integer age;
    private String email;
    private String phone;
    private String profileImg;
    private String suspendReason;
    private List<String> roles;




    public static UserDto fromEntity(UserEntity entity) {
        List<String> roles = Arrays.stream(entity.getRoles().split(","))
                .toList();
        return UserDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .name(entity.getName())
                .age(entity.getAge())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .profileImg(entity.getProfileImg())
                .suspendReason(entity.getSuspendReason())
                .roles(roles)
                .build();
    }
}
