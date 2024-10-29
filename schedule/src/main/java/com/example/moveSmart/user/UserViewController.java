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
    @GetMapping("suspend-req")
    public String suspendAcc(){ return "users/suspend-req"; }
    @GetMapping("update")
    public String update(){ return "users/update"; }

    @GetMapping("plan-create")
    public String test3() {
        return "plan-create";
    }
    @GetMapping("admin/users")
    public String adminGetUsers(){ return "admin/get-users"; }
    @GetMapping("admin/suspend-requests")
    public String adminGetSuspendRequests(){ return "admin/suspend-requests"; }

}
