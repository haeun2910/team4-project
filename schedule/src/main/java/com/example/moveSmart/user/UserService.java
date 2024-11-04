package com.example.moveSmart.user;

import com.example.moveSmart.FileHandlerUtils;
import com.example.moveSmart.admin.dto.UserSuspendDto;
import com.example.moveSmart.auth.jwt.JwtTokenUtils;
import com.example.moveSmart.auth.jwt.dto.JwtRequestDto;
import com.example.moveSmart.auth.jwt.dto.JwtResponseDto;
import com.example.moveSmart.user.dto.CreateUserDto;
import com.example.moveSmart.user.dto.UpdateUserDto;
import com.example.moveSmart.user.dto.UserDto;
import com.example.moveSmart.user.entity.UserSuspend;
import com.example.moveSmart.user.repo.UserRepo;
import com.example.moveSmart.user.entity.ScheduleUserDetails;
import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.repo.UserSuspendRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final AuthenticationFacade authFacade;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final FileHandlerUtils fileHandlerUtils;
    private final UserSuspendRepo userSuspendRepo;

    @Transactional
    public UserDto createUser(CreateUserDto dto) {
        if (!dto.getPassword().equals(dto.getPasswordCheck()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (userRepo.existsByUsername(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        return UserDto.fromEntity(userRepo.save(UserEntity.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build()));
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .map(ScheduleUserDetails::fromEntity)
                .orElseThrow(() -> new UsernameNotFoundException("not found"));
    }
    public JwtResponseDto signin(JwtRequestDto dto) {
        log.info("here");
        UserEntity userEntity = userRepo.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (!passwordEncoder.matches(
                dto.getPassword(),
                userEntity.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        String jwt = jwtTokenUtils.generateToken(ScheduleUserDetails.fromEntity(userEntity));
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);
        return response;
    }

    public UserDto updateUser(UpdateUserDto dto) {
        UserEntity userEntity = authFacade.extractUser();
        userEntity.setName(dto.getName());
        userEntity.setAge(dto.getAge());
        userEntity.setEmail(dto.getEmail());
        userEntity.setPhone(dto.getPhone());
        if (
                userEntity.getName() != null &&
                        userEntity.getAge() != null &&
                        userEntity.getEmail() != null &&
                        userEntity.getPhone() != null &&
                        userEntity.getRoles().equals("ROLE_INACTIVE")
        )
            userEntity.setRoles("ROLE_ACTIVE");
        return UserDto.fromEntity(userRepo.save(userEntity));
    }

    public UserDto profileImg(MultipartFile file) {
        UserEntity userEntity = authFacade.extractUser();
        String requestPath = fileHandlerUtils.saveFile(
                String.format("users/%d/", userEntity.getId()), "profile", file
        );
        userEntity.setProfileImg(requestPath);
        return UserDto.fromEntity(userRepo.save(userEntity));
    }
    public UserDto getUserInfo(){
        UserEntity userEntity = authFacade.extractUser();
        return UserDto.fromEntity(userEntity);
    }

    public void suspendRequest(UserSuspendDto dto){
        UserEntity target = authFacade.extractUser();
        boolean alreadyRequested = userSuspendRepo.existsByTarget(target);
        if (alreadyRequested) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already submitted a suspension request.");
        }
        if (target.getRoles().contains("ROLE_SUSPEND"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        target.setSuspendReason(dto.getSuspendReason());
        target.setSuspendStartDate(LocalDateTime.now());
        userSuspendRepo.save(UserSuspend.builder()
                .target(target)
                        .suspendReason(target.getSuspendReason())
                        .suspendStartDate(target.getSuspendStartDate())
                .build()
        );
    }

    public UserDto comebackUser(){
        UserEntity comeback = authFacade.extractUser();
        log.info("Extracted User: {}", comeback);
        if (!comeback.getRoles().contains("ROLE_SUSPEND")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else {
            comeback.setRoles("ROLE_ACTIVE");

        }
        UserSuspend userSuspend = userSuspendRepo.findByTarget(comeback)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Suspension record not found."));
        userSuspend.setSuspended(false);
//        userSuspendRepo.save(userSuspend);
        userSuspendRepo.delete(userSuspend);
        return UserDto.fromEntity(userRepo.save(comeback));

    }

    public void makeUser(String username, String password, String passCheck) {
        if (userExists(username) || !password.equals(passCheck))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        UserEntity newUser = new UserEntity();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        userRepo.save(newUser);
    }
    public boolean userExists(String username) {
        return userRepo.existsByUsername(username);
    }
}
