package com.example.schedule.auth.config;



import com.example.schedule.auth.jwt.JwtTokenFilter;
import com.example.schedule.auth.jwt.JwtTokenUtils;
import com.example.schedule.oauth.OAuth2SuccessHandler;
import com.example.schedule.oauth.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService manager;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error", "/static/**", "/views/**", "/","/test/**", "/signin/validate", "/oauth-signin")
                            .permitAll();
                    auth.requestMatchers("/users/signup","/users/signin").anonymous();
                    auth.requestMatchers(
                                    "/users/signup-final",
                                    "/users/profile-img",
                                    "/users/get-user-info",
                                    "/users/suspend"
                            )
                            .authenticated();
                    auth.requestMatchers("/api/**").hasAnyRole("ADMIN","ACTIVE");
                    auth.requestMatchers("/admin/**").hasRole("ADMIN");

                })
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/test")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
//                        .defaultSuccessUrl("/users/get-user-info")
                        .failureUrl("/users/signin?fail")
                        .permitAll())
                .addFilterBefore(
                        new JwtTokenFilter(
                                jwtTokenUtils,
                                manager
                        ),
                        AuthorizationFilter.class
                );
        return http.build();
    }
}
