package com.example.moveSmart.user.repo;

import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.entity.UserSuspend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSuspendRepo extends JpaRepository<UserSuspend, Long> {
    boolean existsByTarget(UserEntity target);
}
