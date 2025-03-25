package com.bsslab.domain.user.dto;

import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.entity.UserProfile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class UserDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoResponse {
        private Long id;
        private String username;
        private String email;
        private String role;
        private String status;
        private String bio;

        public static UserInfoResponse from(User user, UserProfile profile) {
            return UserInfoResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .status(user.getStatus().name())
                    .bio(profile != null ? profile.getBio() : null)
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProfileRequest {
        @Size(max = 500, message = "자기소개는 500자 이내로 작성해주세요.")
        private String bio;
    }
}