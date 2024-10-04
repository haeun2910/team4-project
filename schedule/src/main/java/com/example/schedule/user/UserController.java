package com.example.schedule.user;

import com.example.schedule.auth.jwt.dto.JwtRequestDto;
import com.example.schedule.auth.jwt.dto.JwtResponseDto;
import com.example.schedule.user.dto.CreateUserDto;
import com.example.schedule.user.dto.UpdateUserDto;
import com.example.schedule.user.dto.UserDto;
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
    @PutMapping("stopping")
    public void stoppingReq(@RequestBody UserDto dto){
        userService.stoppingRequest(dto);
    }

}
