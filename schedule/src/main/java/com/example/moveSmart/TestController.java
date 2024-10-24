package com.example.moveSmart;

import com.example.moveSmart.auth.jwt.JwtTokenUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("test")
public class TestController {
    private final JwtTokenUtils jwtTokenUtils;
    public TestController(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }
    @GetMapping
    public String test() {
        return "test";
    }
    @GetMapping("search")
    public String test2() {
        return "navermap";
    }
    @GetMapping("plan-create")
    public String test3() {
        return "createPlan";
    }


}

