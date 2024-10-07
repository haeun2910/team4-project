package com.example.schedule.user.repo;

import com.example.schedule.user.entity.UserEntity;
import com.example.schedule.user.entity.UserSuspend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserSuspendRepo extends JpaRepository<UserSuspend, Long> {
//    @Query("SELECT s " +
//            "FROM UserSuspend s " +
//            "WHERE s.suspendReason IS NOT NULL ")
//    Page<UserSuspend> findSuspendRequest(Pageable pageable);
}
