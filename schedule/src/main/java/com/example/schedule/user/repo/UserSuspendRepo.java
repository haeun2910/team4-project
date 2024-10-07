package com.example.schedule.user.repo;

import com.example.schedule.user.entity.UserSuspend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSuspendRepo extends JpaRepository<UserSuspend, Long> {
}
