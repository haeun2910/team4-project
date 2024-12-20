package com.example.moveSmart.user.dto;


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
