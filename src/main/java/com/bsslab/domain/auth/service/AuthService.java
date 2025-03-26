package com.bsslab.domain.auth.service;

import com.bsslab.domain.auth.dto.LoginRequest;
import com.bsslab.domain.auth.dto.SignupRequest;
import com.bsslab.domain.auth.dto.TokenResponse;
import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.entity.UserProfile;
import com.bsslab.domain.user.repository.UserProfileRepository;
import com.bsslab.domain.user.repository.UserRepository;
import com.bsslab.global.exception.DuplicateResourceException;
import com.bsslab.global.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public void registerUser(SignupRequest signupRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new DuplicateResourceException("Username is already taken!");
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new DuplicateResourceException("Email is already in use!");
        }

        // Create new user account
        User user = User.builder()
                .username(signupRequest.getUsername())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(User.Role.ROLE_USER)
                .status(User.Status.ACTIVE)
                .build();

        userRepository.save(user);

        // Create an empty profile for the user
        UserProfile userProfile = UserProfile.builder()
                .user(user)
                .build();

        userProfileRepository.save(userProfile);
    }

    @Transactional
    public TokenResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return TokenResponse.builder()
                .token(jwt)
                .type("Bearer")
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}