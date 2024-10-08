package com.example.schedule.admin;

import com.example.schedule.admin.dto.UserSuspendDto;
import com.example.schedule.user.dto.UserDto;
import com.example.schedule.user.entity.UserSuspend;
import com.example.schedule.user.repo.UserRepo;
import com.example.schedule.user.repo.UserSuspendRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        suspend.setSuspended(true);
        if (suspend.getSuspendReason() !=null || suspend.getTarget().getRoles().contains("ROLE_ACTIVE")) {
            suspend.getTarget().setRoles("ROLE_SUSPEND");
        }
        return UserSuspendDto.fromEntity(userSuspendRep.save(suspend));

    }



}
