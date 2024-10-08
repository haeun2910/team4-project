package com.example.schedule;

import com.example.schedule.user.entity.UserEntity;
import com.example.schedule.user.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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
//                .username("admin")
//                .password(passwordEncoder.encode("1234"))
//                .name("admin")
//                .age(23)
//                .roles("ROLE_ADMIN")
//                .build();
//        userRepo.save(admin1);
//        UserEntity user1 = UserEntity.builder()
//                .username("user1")
//                .password(passwordEncoder.encode("1234"))
//                .name("alex")
//                .age(30)
//                .email("user1@gmail.com")
//                .phone("01011111111")
//                .roles("ROLE_ACTIVE")
//                .build();
//        UserEntity user2 = UserEntity.builder()
//                .username("user2")
//                .password(passwordEncoder.encode("1234"))
//                .name("brad")
//                .age(30)
//                .email("user2@gmail.com")
//                .phone("01011112222")
//                .roles("ROLE_ACTIVE")
//                .build();
//        UserEntity user3 = UserEntity.builder()
//                .username("user3")
//                .password(passwordEncoder.encode("1234"))
//                .name("chad")
//                .age(30)
//                .email("user3@gmail.com")
//                .phone("01011113333")
//                .roles("ROLE_SUSPEND")
//                .suspendReason("외국 나감")
//                .build();
//        userRepo.saveAll(List.of(
//                user1,
//                user2,
//                user3));
    }
}
