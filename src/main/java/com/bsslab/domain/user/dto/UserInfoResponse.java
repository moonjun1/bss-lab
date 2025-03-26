package com.bsslab.domain.user.dto;

import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.entity.UserProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 정보 응답 DTO")
public class UserInfoResponse {
    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 아이디", example = "bsslab_user")
    private String username;

    @Schema(description = "사용자 이메일", example = "user@bsslab.com")
    private String email;

    @Schema(description = "사용자 역할", example = "ROLE_USER")
    private String role;

    @Schema(description = "사용자 상태", example = "ACTIVE")
    private String status;

    @Schema(description = "자기소개", example = "안녕하세요. BSS-Lab에서 백엔드 개발을 담당하고 있는 개발자입니다.")
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