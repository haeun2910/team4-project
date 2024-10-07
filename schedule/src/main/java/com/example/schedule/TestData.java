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
//        UserEntity admin1 = UserEntity.builder()
//                .username("admin1")
//                .password(passwordEncoder.encode("1234"))
//                .name("admin1")
//                .age(23)
//                .roles("ROLE_ADMIN")
//                .build();
//        userRepo.save(admin1);
    }
}
