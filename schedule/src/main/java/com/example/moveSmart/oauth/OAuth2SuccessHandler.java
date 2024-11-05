package com.example.moveSmart.oauth;

import com.example.moveSmart.auth.jwt.JwtTokenUtils;
import com.example.moveSmart.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final JwtTokenUtils tokenUtils;

    public OAuth2SuccessHandler(UserService userService, JwtTokenUtils tokenUtils ) {
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String provider = oAuth2User.getAttribute("provider");
        String email = oAuth2User.getAttribute("email");
        String username = String.format("{%s}%s", provider, email);
        String providerId = oAuth2User.getAttribute("id").toString();
        if (!userService.userExists(username)) {
            userService.makeUser(username, providerId, providerId, email);
        }
        UserDetails userDetails = userService.loadUserByUsername(username);
        String jwt = tokenUtils.generateToken(userDetails);
        String targetUrl = String.format("http://localhost:8080/users/validate?token=%s", jwt);
        log.info(targetUrl);
        getRedirectStrategy().sendRedirect(request,response,targetUrl); 
    }

}
