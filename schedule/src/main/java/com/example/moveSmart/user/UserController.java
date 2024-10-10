package com.example.moveSmart.user;

import com.example.moveSmart.admin.dto.UserSuspendDto;
import com.example.moveSmart.auth.jwt.dto.JwtRequestDto;
import com.example.moveSmart.auth.jwt.dto.JwtResponseDto;
import com.example.moveSmart.user.dto.CreateUserDto;
import com.example.moveSmart.user.dto.UpdateUserDto;
import com.example.moveSmart.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("signup")
    public UserDto signup(@RequestBody CreateUserDto dto) {
        return userService.createUser(dto);
    }
    @PostMapping("signin")
    public JwtResponseDto signin(@RequestBody JwtRequestDto dto) {
        return userService.signin(dto);
    }

    @PutMapping("signup-final")
    public UserDto signupFinal(@RequestBody UpdateUserDto dto) {
        return userService.updateUser(dto);
    }
    @PutMapping(
            value = "profile-img",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public UserDto profileImg(@RequestParam("file") MultipartFile file) {
        return userService.profileImg(file);
    }
    @GetMapping("get-user-info")
    public UserDto getUserInfo(){
        return userService.getUserInfo();
    }
    @PutMapping("suspend-req")
    public void suspendReq(@RequestBody UserSuspendDto dto) {
        userService.suspendRequest(dto);
    }
    @PutMapping("comeback")
    public UserDto comeback() {
        return userService.comebackUser();
    }
}
