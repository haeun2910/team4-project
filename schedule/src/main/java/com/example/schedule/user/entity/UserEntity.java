package com.example.schedule.user.entity;

import com.example.schedule.entity.BaseEntity;
import com.example.schedule.schedule.entity.Schedule;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class UserEntity extends BaseEntity {
    @Setter
    @Column(unique = true)
    private String username;
    @Setter
    private String password;
    @Setter
    private String name;
    @Setter
    private Integer age;
    @Setter
    @Column(unique = true)
    private String email;
    @Setter
    @Column(unique = true)
    private String phone;
    @Setter
    private String profileImg;
    @Setter
    private String suspendReason;
    @Setter
    @Builder.Default
    private String roles = "ROLE_INACTIVE";
    @OneToMany(mappedBy = "user")
    private final List<Schedule> schedules = new ArrayList<>();

}
