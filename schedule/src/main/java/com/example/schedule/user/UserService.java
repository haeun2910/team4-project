package com.example.schedule.user;

import com.example.schedule.FileHandlerUtils;
import com.example.schedule.admin.dto.UserSuspendDto;
import com.example.schedule.auth.jwt.JwtTokenUtils;
import com.example.schedule.auth.jwt.dto.JwtRequestDto;
import com.example.schedule.auth.jwt.dto.JwtResponseDto;
import com.example.schedule.user.dto.CreateUserDto;
import com.example.schedule.user.dto.UpdateUserDto;
import com.example.schedule.user.dto.UserDto;
import com.example.schedule.user.entity.UserSuspend;
import com.example.schedule.user.repo.UserRepo;
import com.example.schedule.user.entity.ScheduleUserDetails;
import com.example.schedule.user.entity.UserEntity;
import com.example.schedule.user.repo.UserSuspendRepo;
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
        if (target.getRoles().contains("ROLE_SUSPEND"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        target.setSuspendReason(dto.getSuspendReason());
        userSuspendRepo.save(UserSuspend.builder()
                .target(target)
                        .suspendReason(target.getSuspendReason())
                .build()
        );
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
