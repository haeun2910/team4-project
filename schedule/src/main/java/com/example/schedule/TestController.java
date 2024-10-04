package com.example.schedule;

import com.example.schedule.auth.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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


}

