package com.vr.job.service.impl;

import com.vr.job.domain.UserRole;
import com.vr.job.domain.UserStatus;
import com.vr.job.mapper.UserMapper;
import com.vr.job.modal.User;
import com.vr.job.payload.AuthResponse;
import com.vr.job.payload.LoginRequest;
import com.vr.job.payload.SignupRequest;
import com.vr.job.repository.UserRepository;
import com.vr.job.security.CustomUserDetailsService;
import com.vr.job.security.JwtProvider;
import com.vr.job.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public AuthResponse signup(SignupRequest request) throws Exception {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new Exception("Email Already registered ! " + request.getEmail());
        }

        if (request.getRole() == UserRole.ROLE_ADMIN) {
            throw new Exception("Cannot self-register as role admin");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .phone(request.getPhone())
                .lastLog(LocalDateTime.now())
                .status(UserStatus.ACTIVE)
                .build();

        User saveUser = userRepository.save(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication, user.getId());

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome " + saveUser.getFullName());
        response.setMessage("Registered successfully.");
        response.setJwt(jwt);
        response.setUser(UserMapper.toDTO(saveUser));

        return response;
    }

    @Override
    public AuthResponse login(LoginRequest request) throws Exception {

        Authentication authentication = authenticate(
                request.getEmail(), request.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail());

        String jwt = jwtProvider.generateToken(authentication, user.getId());

        user.setLastLog(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setTitle("Welcome back - " + user.getFullName());
        response.setMessage("Login successfully.");
        response.setJwt(jwt);
        response.setUser(UserMapper.toDTO(user));

        return response;
    }

    private Authentication authenticate(String email, String password) throws Exception {

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new Exception("User not found with email " + email);
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new Exception("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }
}
