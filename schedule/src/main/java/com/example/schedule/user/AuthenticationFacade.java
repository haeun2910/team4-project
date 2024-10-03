package com.example.schedule.user;

import com.example.schedule.user.entity.ScheduleUserDetails;
import com.example.schedule.user.entity.UserEntity;
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
