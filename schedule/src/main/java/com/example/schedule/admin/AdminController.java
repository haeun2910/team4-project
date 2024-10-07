package com.example.schedule.admin;

import com.example.schedule.admin.dto.UserSuspendDto;
import com.example.schedule.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    @GetMapping("users")
    public Page<UserDto> readAllUsers(Pageable pageable) {
        return service.readAllUser(pageable);
    }
    @GetMapping("suspend-request")
    public Page<UserDto> readAllStoppingRequests(Pageable pageable) {
        return service.readSuspendRequests(pageable);
    }
    @PutMapping("approveSuspend/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserSuspendDto approveSuspend(
            @PathVariable("id")
            Long id) {
        return service.approveSuspend(id);
    }
}
