package com.bsslab.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로필 업데이트 요청 DTO")
public class UpdateProfileRequest {
    @Size(max = 500, message = "자기소개는 500자 이내로 작성해주세요.")
    @Schema(description = "자기소개 (최대 500자)", example = "안녕하세요. BSS-Lab에서 백엔드 개발을 담당하고 있는 개발자입니다. Spring Boot, JPA, MySQL 등의 기술 스택을 활용한 웹 애플리케이션 개발에 관심이 있습니다.")
    private String bio;
}