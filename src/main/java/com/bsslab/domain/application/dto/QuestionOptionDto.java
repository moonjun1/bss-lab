package com.bsslab.domain.application.dto;

import com.bsslab.domain.application.entity.QuestionOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QuestionOption 관련 DTO 클래스 모음
 */
public class QuestionOptionDto {

    /**
     * 질문 옵션 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "질문 옵션 생성 요청")
    public static class CreateRequest {
        @NotBlank(message = "옵션 내용은 필수 입력값입니다.")
        @Schema(description = "옵션 내용", example = "1-3년")
        private String content;

        @Schema(description = "옵션 순서", example = "1")
        private Integer optionOrder;
    }

    /**
     * 질문 옵션 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "질문 옵션 수정 요청")
    public static class UpdateRequest {
        @Schema(description = "옵션 내용", example = "1-3년 (수정됨)")
        private String content;

        @Schema(description = "옵션 순서", example = "2")
        private Integer optionOrder;
    }

    /**
     * 질문 옵션 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "질문 옵션 응답")
    public static class Response {
        @Schema(description = "옵션 ID", example = "1")
        private Long id;

        @Schema(description = "옵션 내용", example = "1-3년")
        private String content;

        @Schema(description = "옵션 순서", example = "1")
        private Integer optionOrder;

        /**
         * 엔티티에서 DTO 생성
         */
        public static Response from(QuestionOption option) {
            return Response.builder()
                    .id(option.getId())
                    .content(option.getContent())
                    .optionOrder(option.getOptionOrder())
                    .build();
        }
    }
}