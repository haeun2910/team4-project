package com.example.moveSmart.admin;

import com.example.moveSmart.admin.dto.UserSuspendDto;
import com.example.moveSmart.user.dto.UserDto;
import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.entity.UserSuspend;
import com.example.moveSmart.user.repo.UserRepo;
import com.example.moveSmart.user.repo.UserSuspendRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepo userRepo;
    private final UserSuspendRepo userSuspendRep;

    public Page<UserDto> readAllUser(Pageable pageable) {
        return userRepo.findAll(pageable).map(UserDto::fromEntity);
    }

    public Page<UserSuspendDto> readSuspendRequests(Pageable pageable) {
        return userSuspendRep.findAll(pageable).map(UserSuspendDto::fromEntity);
    }

    @Transactional
    public UserSuspendDto approveSuspend(Long id) {
        UserSuspend suspend = userSuspendRep.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suspension request not found."));

        // Mark the suspension as approved
        suspend.setSuspended(true);

        // Update the user's roles if the suspension reason is valid
        UserEntity targetUser = suspend.getTarget();
        if (suspend.getSuspendReason() != null && targetUser.getRoles().contains("ROLE_ACTIVE")) {
            targetUser.setRoles("ROLE_SUSPEND");
            targetUser.setSuspendStartDate(LocalDateTime.now()); // Optionally reset start date on approval
        }

        // Save the updated UserSuspend and UserEntity
        userSuspendRep.save(suspend);
        userRepo.save(targetUser); // Ensure the UserEntity is also updated

        return UserSuspendDto.fromEntity(suspend);
    }
}
