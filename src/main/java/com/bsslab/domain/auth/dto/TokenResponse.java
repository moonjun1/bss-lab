package com.bsslab.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // 이 어노테이션은 getter, setter, equals, hashCode, toString을 자동 생성합니다
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인증 토큰 응답 DTO")
public class TokenResponse {
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJic3NsYWJfdXNlciIsImlhdCI6MTYxNjEyMzQ1NiwiZXhwIjoxNjE2MjA5ODU2fQ.kZ5pCJqH9J6KZ1B7X...")
    private String token;

    @Schema(description = "토큰 타입", example = "Bearer")
    private String type;

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "사용자 아이디", example = "bsslab_user")
    private String username;

    @Schema(description = "사용자 이메일", example = "user@bsslab.com")
    private String email;

    @Schema(description = "사용자 역할", example = "ROLE_USER")
    private String role;
}