package com.example.moveSmart.user.repo;

import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.entity.UserSuspend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserSuspendRepo extends JpaRepository<UserSuspend, Long> {
    boolean existsByTarget(UserEntity target);
    void deleteByTarget(UserEntity target);
    Optional<UserSuspend> findByTarget(UserEntity target);
}
