package com.example.schedule.user.repo;

import com.example.schedule.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    @Query("SELECT s " +
            "FROM UserEntity s " +
            "WHERE s.suspendReason IS NOT NULL ")
    Page<UserEntity> findSuspendRequest(Pageable pageable);
}
