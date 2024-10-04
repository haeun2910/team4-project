package com.example.schedule.user.entity;

import com.example.schedule.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
    private String stoppingReason;
    @Setter
    @Builder.Default
    private String roles = "ROLE_INACTIVE";

}
