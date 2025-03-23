package com.bsslab.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원가입 요청 데이터")
    public static class SignupRequest {
        @Schema(description = "사용자 아이디", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;

        @Schema(description = "이메일 주소", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        private String email;

        @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 요청 데이터")
    public static class LoginRequest {
        @Schema(description = "사용자 아이디", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Username is required")
        private String username;

        @Schema(description = "비밀번호", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "인증 토큰 응답 데이터")
    public static class TokenResponse {
        @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwiaWF0IjoxNjQ2OTIwMjQ4LCJleHAiOjE2NDcwMDY2NDh9.abcdef123456")
        private String token;

        @Schema(description = "토큰 타입", example = "Bearer")
        private String type;

        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "사용자 아이디", example = "user123")
        private String username;

        @Schema(description = "이메일 주소", example = "user@example.com")
        private String email;

        @Schema(description = "사용자 역할", example = "ROLE_USER")
        private String role;
    }
}