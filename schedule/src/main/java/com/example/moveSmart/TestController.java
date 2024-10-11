package com.example.moveSmart;

import com.example.moveSmart.auth.jwt.JwtTokenUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {
    private final JwtTokenUtils jwtTokenUtils;
    public TestController(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }
    @GetMapping("test")
    public String test() {
        return "test";
    }
    @GetMapping
    public String test2() {
        return "naver-map";
    }


}

