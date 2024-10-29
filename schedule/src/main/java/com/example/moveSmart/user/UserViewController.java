package com.example.moveSmart.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("views")
public class UserViewController {
    @GetMapping
    public String home(){ return "users/index"; }
    @GetMapping("signup")
    public String signUp(){ return "users/signup";}
    @GetMapping("signin")
    public String signIn(){ return "users/signin"; }
    @GetMapping("update")
    public String update(){ return "users/update"; }
    @GetMapping("plan-create")
    public String planCreate() {
        return "/plan/plan-create";
    }
    @GetMapping("my-plan")
    public String myPlan() {
        return "/plan/my-plan";
    }
    @GetMapping("view-plan")
    public String readOnePlan(){
        return "/plan/view-plan";
    }
    @GetMapping("pub-trans-route")
    public String readOneRoute(){
        return "/route/view-pub-trans-route";
    }
}
