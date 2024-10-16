package com.example.moveSmart.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String name;
    private Integer age;
    private String email;
    private String phone;
}
