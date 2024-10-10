package com.example.moveSmart.user.repo;

import com.example.moveSmart.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
//    @Query("SELECT s " +
//            "FROM UserEntity s " +
//            "WHERE s.suspendReason IS NOT NULL ")
//    Page<UserEntity> findSuspendRequest(Pageable pageable);

}
