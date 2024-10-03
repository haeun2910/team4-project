package com.example.schedule;

import com.example.schedule.user.entity.UserEntity;
import com.example.schedule.user.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TestData {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    public TestData(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        testUsers();

    }

    private void testUsers(){
    }
}
