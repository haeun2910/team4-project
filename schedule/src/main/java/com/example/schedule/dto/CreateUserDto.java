package com.example.schedule.dto;


import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDto {
    private String username;
    private String password;
    private String passwordCheck;
}
