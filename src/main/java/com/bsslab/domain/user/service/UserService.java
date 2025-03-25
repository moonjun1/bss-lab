package com.bsslab.domain.user.service;

import com.bsslab.domain.user.dto.UserDto;
import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.entity.UserProfile;
import com.bsslab.domain.user.repository.UserProfileRepository;
import com.bsslab.domain.user.repository.UserRepository;
import com.bsslab.global.exception.GlobalExceptionHandler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserDto.UserInfoResponse getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElse(null);

        return UserDto.UserInfoResponse.from(user, profile);
    }

    @Transactional
    public void updateProfile(String username, UserDto.UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        UserProfile profile = userProfileRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found for user: " + username));

        profile.setBio(request.getBio());
        userProfileRepository.save(profile);
    }
}