package com.example.moveSmart.user.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Integer age;
    private String email;
    private String phone;
    private String profileImg;
    private String rolesRaw;
    @Getter
    private UserEntity entity;
    public static ScheduleUserDetails fromEntity(UserEntity entity) {
        return ScheduleUserDetails.builder()
                .entity(entity)
                .build();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(entity.getRoles().split(","))
                .map(role -> (GrantedAuthority) () -> role)
                .toList();
    }

    @Override
    public String getPassword() { return this.entity.getPassword();}

    @Override
    public String getUsername() { return this.entity.getUsername();}

    public Long getId() {
        return this.entity.getId();
    }
}
