package com.bsslab.domain.application.dto;

import com.bsslab.domain.application.entity.ApplicationForm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApplicationForm 관련 DTO 클래스 모음
 */
public class ApplicationFormDto {

    /**
     * 지원 양식 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 양식 생성 요청")
    public static class CreateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 2, max = 100, message = "제목은 2~100자 사이여야 합니다.")
        @Schema(description = "지원 양식 제목", example = "2025년 BSS-Lab 연구원 모집")
        private String title;

        @Schema(description = "지원 양식 설명", example = "BSS-Lab에서 2025년 연구원을 모집합니다. 지원 자격 및 조건을 확인하시고 지원해주세요.")
        private String description;

        @Schema(description = "지원 양식 상태", example = "DRAFT", allowableValues = {"DRAFT", "PUBLISHED", "CLOSED"})
        private ApplicationForm.Status status;

        @Schema(description = "지원 시작 일시", example = "2025-01-01T09:00:00")
        private LocalDateTime startDate;

        @Schema(description = "지원 마감 일시", example = "2025-01-31T18:00:00")
        private LocalDateTime endDate;

        @Schema(description = "질문 목록")
        private List<QuestionDto.CreateRequest> questions;
    }

    /**
     * 지원 양식 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 양식 수정 요청")
    public static class UpdateRequest {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 2, max = 100, message = "제목은 2~100자 사이여야 합니다.")
        @Schema(description = "지원 양식 제목", example = "2025년 BSS-Lab 연구원 모집 (수정)")
        private String title;

        @Schema(description = "지원 양식 설명", example = "BSS-Lab에서 2025년 연구원을 모집합니다. 지원 자격 및 조건을 확인하시고 지원해주세요. (수정됨)")
        private String description;

        @Schema(description = "지원 양식 상태", example = "PUBLISHED", allowableValues = {"DRAFT", "PUBLISHED", "CLOSED"})
        private ApplicationForm.Status status;

        @Schema(description = "지원 시작 일시", example = "2025-01-01T09:00:00")
        private LocalDateTime startDate;

        @Schema(description = "지원 마감 일시", example = "2025-01-31T18:00:00")
        private LocalDateTime endDate;
    }

    /**
     * 지원 양식 간략 정보 응답 DTO (목록 조회용)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 양식 목록 응답")
    public static class ListResponse {
        @Schema(description = "지원 양식 ID", example = "1")
        private Long id;

        @Schema(description = "지원 양식 제목", example = "2025년 BSS-Lab 연구원 모집")
        private String title;

        @Schema(description = "지원 양식 설명", example = "BSS-Lab에서 2025년 연구원을 모집합니다. 지원 자격 및 조건을 확인하시고 지원해주세요.")
        private String description;

        @Schema(description = "지원 양식 상태", example = "PUBLISHED")
        private ApplicationForm.Status status;

        @Schema(description = "지원 시작 일시", example = "2025-01-01T09:00:00")
        private LocalDateTime startDate;

        @Schema(description = "지원 마감 일시", example = "2025-01-31T18:00:00")
        private LocalDateTime endDate;

        @Schema(description = "질문 수", example = "10")
        private Integer questionCount;

        @Schema(description = "지원서 제출 수", example = "42")
        private Integer applicationCount;

        @Schema(description = "생성 일시", example = "2024-12-15T10:30:00")
        private LocalDateTime createdAt;

        /**
         * 엔티티에서 DTO 생성
         */
        public static ListResponse from(ApplicationForm form) {
            return ListResponse.builder()
                    .id(form.getId())
                    .title(form.getTitle())
                    .description(form.getDescription())
                    .status(form.getStatus())
                    .startDate(form.getStartDate())
                    .endDate(form.getEndDate())
                    .questionCount(form.getQuestions() != null ? form.getQuestions().size() : 0)
                    .applicationCount(form.getApplications() != null ? form.getApplications().size() : 0)
                    .createdAt(form.getCreatedAt())
                    .build();
        }
    }

    /**
     * 지원 양식 상세 정보 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 양식 상세 응답")
    public static class DetailResponse {
        @Schema(description = "지원 양식 ID", example = "1")
        private Long id;

        @Schema(description = "지원 양식 제목", example = "2025년 BSS-Lab 연구원 모집")
        private String title;

        @Schema(description = "지원 양식 설명", example = "BSS-Lab에서 2025년 연구원을 모집합니다. 지원 자격 및 조건을 확인하시고 지원해주세요.")
        private String description;

        @Schema(description = "지원 양식 상태", example = "PUBLISHED")
        private ApplicationForm.Status status;

        @Schema(description = "지원 시작 일시", example = "2025-01-01T09:00:00")
        private LocalDateTime startDate;

        @Schema(description = "지원 마감 일시", example = "2025-01-31T18:00:00")
        private LocalDateTime endDate;

        @Schema(description = "질문 목록")
        private List<QuestionDto.Response> questions;

        @Schema(description = "생성 일시", example = "2024-12-15T10:30:00")
        private LocalDateTime createdAt;

        @Schema(description = "수정 일시", example = "2024-12-16T14:20:00")
        private LocalDateTime updatedAt;

        /**
         * 엔티티에서 DTO 생성
         */
        public static DetailResponse from(ApplicationForm form) {
            List<QuestionDto.Response> questionResponses = form.getQuestions().stream()
                    .map(QuestionDto.Response::from)
                    .collect(Collectors.toList());

            return DetailResponse.builder()
                    .id(form.getId())
                    .title(form.getTitle())
                    .description(form.getDescription())
                    .status(form.getStatus())
                    .startDate(form.getStartDate())
                    .endDate(form.getEndDate())
                    .questions(questionResponses)
                    .createdAt(form.getCreatedAt())
                    .updatedAt(form.getUpdatedAt())
                    .build();
        }
    }
}