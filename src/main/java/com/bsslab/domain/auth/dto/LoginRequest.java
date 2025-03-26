package com.bsslab.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // 이 어노테이션은 getter, setter, equals, hashCode, toString을 자동 생성합니다
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Schema(description = "사용자 아이디", example = "bsslab_user")
    private String username;

    @NotBlank(message = "Password is required")
    @Schema(description = "비밀번호", example = "securePassword123")
    private String password;
}