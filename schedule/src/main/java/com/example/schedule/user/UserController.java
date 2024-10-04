package com.example.schedule.user;

import com.example.schedule.auth.jwt.dto.JwtRequestDto;
import com.example.schedule.auth.jwt.dto.JwtResponseDto;
import com.example.schedule.user.dto.CreateUserDto;
import com.example.schedule.user.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("oauth-signin")
    public Map<String, Object> getOAuthSign(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return oAuth2User.getAttributes();

    }

}
