package com.bsslab.domain.application.dto;

import com.bsslab.domain.application.entity.Application;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application 관련 DTO 클래스 모음
 */
public class ApplicationDto {

    /**
     * 지원서 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원서 생성 요청")
    public static class CreateRequest {
        @NotNull(message = "지원 양식 ID는 필수 입력값입니다.")
        @Schema(description = "지원 양식 ID", example = "1")
        private Long applicationFormId;

        @NotBlank(message = "지원자 이름은 필수 입력값입니다.")
        @Schema(description = "지원자 이름", example = "홍길동")
        private String applicantName;

        @NotBlank(message = "지원자 이메일은 필수 입력값입니다.")
        @Email(message = "유효한 이메일 형식이어야 합니다.")
        @Schema(description = "지원자 이메일", example = "hong@example.com")
        private String applicantEmail;

        @Schema(description = "지원자 전화번호", example = "010-1234-5678")
        private String applicantPhone;

        @Schema(description = "지원서 상태", example = "DRAFT", defaultValue = "DRAFT",
                allowableValues = {"DRAFT", "SUBMITTED"})
        private Application.Status status;

        @Schema(description = "답변 목록")
        private List<ApplicationAnswerDto.CreateRequest> answers;
    }

    /**
     * 지원서 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원서 수정 요청")
    public static class UpdateRequest {
        @Schema(description = "지원자 이름", example = "홍길동")
        private String applicantName;

        @Email(message = "유효한 이메일 형식이어야 합니다.")
        @Schema(description = "지원자 이메일", example = "hong@example.com")
        private String applicantEmail;

        @Schema(description = "지원자 전화번호", example = "010-1234-5678")
        private String applicantPhone;

        @Schema(description = "지원서 상태", example = "SUBMITTED",
                allowableValues = {"DRAFT", "SUBMITTED"})
        private Application.Status status;

        @Schema(description = "답변 목록")
        private List<ApplicationAnswerDto.UpdateRequest> answers;
    }

    /**
     * 지원서 간략 정보 응답 DTO (목록 조회용)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원서 목록 응답")
    public static class ListResponse {
        @Schema(description = "지원서 ID", example = "1")
        private Long id;

        @Schema(description = "지원 양식 ID", example = "1")
        private Long applicationFormId;

        @Schema(description = "지원 양식 제목", example = "2025년 BSS-Lab 연구원 모집")
        private String applicationFormTitle;

        @Schema(description = "지원자 이름", example = "홍길동")
        private String applicantName;

        @Schema(description = "지원자 이메일", example = "hong@example.com")
        private String applicantEmail;

        @Schema(description = "지원서 상태", example = "SUBMITTED")
        private Application.Status status;

        @Schema(description = "제출 일시", example = "2025-01-15T14:30:00")
        private LocalDateTime submittedAt;

        @Schema(description = "검토 일시", example = "2025-01-20T10:15:00")
        private LocalDateTime reviewedAt;

        /**
         * 엔티티에서 DTO 생성
         */
        public static ListResponse from(Application application) {
            return ListResponse.builder()
                    .id(application.getId())
                    .applicationFormId(application.getApplicationForm().getId())
                    .applicationFormTitle(application.getApplicationForm().getTitle())
                    .applicantName(application.getApplicantName())
                    .applicantEmail(application.getApplicantEmail())
                    .status(application.getStatus())
                    .submittedAt(application.getSubmittedAt())
                    .reviewedAt(application.getReviewedAt())
                    .build();
        }
    }

    /**
     * 지원서 상세 정보 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원서 상세 응답")
    public static class DetailResponse {
        @Schema(description = "지원서 ID", example = "1")
        private Long id;

        @Schema(description = "지원 양식 ID", example = "1")
        private Long applicationFormId;

        @Schema(description = "지원 양식 제목", example = "2025년 BSS-Lab 연구원 모집")
        private String applicationFormTitle;

        @Schema(description = "지원자 이름", example = "홍길동")
        private String applicantName;

        @Schema(description = "지원자 이메일", example = "hong@example.com")
        private String applicantEmail;

        @Schema(description = "지원자 전화번호", example = "010-1234-5678")
        private String applicantPhone;

        @Schema(description = "지원서 상태", example = "SUBMITTED")
        private Application.Status status;

        @Schema(description = "제출 일시", example = "2025-01-15T14:30:00")
        private LocalDateTime submittedAt;

        @Schema(description = "검토 일시", example = "2025-01-20T10:15:00")
        private LocalDateTime reviewedAt;

        @Schema(description = "검토자 코멘트", example = "서류 검토 완료, 면접 대상자로 선정")
        private String reviewerComment;

        @Schema(description = "답변 목록")
        private List<ApplicationAnswerDto.Response> answers;

        @Schema(description = "생성 일시", example = "2025-01-10T11:20:00")
        private LocalDateTime createdAt;

        @Schema(description = "수정 일시", example = "2025-01-15T14:30:00")
        private LocalDateTime updatedAt;

        /**
         * 엔티티에서 DTO 생성
         */
        public static DetailResponse from(Application application) {
            List<ApplicationAnswerDto.Response> answerResponses = application.getAnswers().stream()
                    .map(ApplicationAnswerDto.Response::from)
                    .collect(Collectors.toList());

            return DetailResponse.builder()
                    .id(application.getId())
                    .applicationFormId(application.getApplicationForm().getId())
                    .applicationFormTitle(application.getApplicationForm().getTitle())
                    .applicantName(application.getApplicantName())
                    .applicantEmail(application.getApplicantEmail())
                    .applicantPhone(application.getApplicantPhone())
                    .status(application.getStatus())
                    .submittedAt(application.getSubmittedAt())
                    .reviewedAt(application.getReviewedAt())
                    .reviewerComment(application.getReviewerComment())
                    .answers(answerResponses)
                    .createdAt(application.getCreatedAt())
                    .updatedAt(application.getUpdatedAt())
                    .build();
        }
    }

    /**
     * 지원서 상태 변경 요청 DTO (관리자용)
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원서 상태 변경 요청 (관리자용)")
    public static class StatusUpdateRequest {
        @NotNull(message = "지원서 상태는 필수 입력값입니다.")
        @Schema(description = "변경할 상태", example = "UNDER_REVIEW",
                allowableValues = {"UNDER_REVIEW", "ACCEPTED", "REJECTED", "CANCELLED"})
        private Application.Status status;

        @Schema(description = "검토자 코멘트", example = "서류 검토 완료, 면접 대상자로 선정")
        private String reviewerComment;
    }
}