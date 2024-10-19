package com.example.moveSmart.user;

import com.example.moveSmart.user.entity.ScheduleUserDetails;
import com.example.moveSmart.user.entity.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {
    public Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    public UserEntity extractUser() {
        ScheduleUserDetails userDetails = (ScheduleUserDetails)getAuth().getPrincipal();
        return userDetails.getEntity();
    }
}
