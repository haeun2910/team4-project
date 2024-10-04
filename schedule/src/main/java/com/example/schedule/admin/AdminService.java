package com.example.schedule.admin;

import com.example.schedule.user.dto.UserDto;
import com.example.schedule.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepo userRepo;

    public Page<UserDto> readAllUser(Pageable pageable) {
        return userRepo.findAll(pageable).map(UserDto::fromEntity);
    }
    public Page<UserDto> readSuspendRequests(Pageable pageable) {
        return userRepo.findSuspendRequest(pageable).map(UserDto::fromEntity);
    }
}
