package com.bsslab.domain.application.dto;

import com.bsslab.domain.application.entity.Question;
import com.bsslab.domain.application.entity.QuestionOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Question 관련 DTO 클래스 모음
 */
public class QuestionDto {

    /**
     * 질문 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "질문 생성 요청")
    public static class CreateRequest {
        @NotNull(message = "질문 유형은 필수 입력값입니다.")
        @Schema(description = "질문 유형", example = "SHORT_TEXT",
                allowableValues = {"SHORT_TEXT", "LONG_TEXT", "SINGLE_CHOICE",
                        "MULTIPLE_CHOICE", "DROPDOWN", "DATE",
                        "EMAIL", "PHONE", "NUMBER"})
        private Question.QuestionType questionType;

        @NotBlank(message = "질문 내용은 필수 입력값입니다.")
        @Schema(description = "질문 내용", example = "자기소개를 해주세요.")
        private String content;

        @Schema(description = "필수 응답 여부", example = "true", defaultValue = "false")
        private Boolean required;

        @Schema(description = "질문 순서", example = "1")
        private Integer questionOrder;

        @Schema(description = "입력란 안내 텍스트", example = "간략한 자기소개를 입력해주세요.")
        private String placeholder;

        @Schema(description = "질문에 대한 도움말", example = "연구 분야, 경력, 관심사 등을 포함해주세요.")
        private String helpText;

        @Schema(description = "객관식 옵션 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN 유형에만 필요)")
        private List<QuestionOptionDto.CreateRequest> options;
    }

    /**
     * 질문 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "질문 수정 요청")
    public static class UpdateRequest {
        @Schema(description = "질문 유형", example = "SHORT_TEXT",
                allowableValues = {"SHORT_TEXT", "LONG_TEXT", "SINGLE_CHOICE",
                        "MULTIPLE_CHOICE", "DROPDOWN", "DATE",
                        "EMAIL", "PHONE", "NUMBER"})
        private Question.QuestionType questionType;

        @Schema(description = "질문 내용", example = "자기소개를 해주세요. (수정됨)")
        private String content;

        @Schema(description = "필수 응답 여부", example = "true")
        private Boolean required;

        @Schema(description = "질문 순서", example = "2")
        private Integer questionOrder;

        @Schema(description = "입력란 안내 텍스트", example = "간략한 자기소개를 입력해주세요. (수정됨)")
        private String placeholder;

        @Schema(description = "질문에 대한 도움말", example = "연구 분야, 경력, 관심사 등을 포함해주세요. (수정됨)")
        private String helpText;
    }

    /**
     * 질문 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "질문 응답")
    public static class Response {
        @Schema(description = "질문 ID", example = "1")
        private Long id;

        @Schema(description = "질문 유형", example = "SHORT_TEXT")
        private Question.QuestionType questionType;

        @Schema(description = "질문 내용", example = "자기소개를 해주세요.")
        private String content;

        @Schema(description = "필수 응답 여부", example = "true")
        private Boolean required;

        @Schema(description = "질문 순서", example = "1")
        private Integer questionOrder;

        @Schema(description = "입력란 안내 텍스트", example = "간략한 자기소개를 입력해주세요.")
        private String placeholder;

        @Schema(description = "질문에 대한 도움말", example = "연구 분야, 경력, 관심사 등을 포함해주세요.")
        private String helpText;

        @Schema(description = "객관식 옵션 목록")
        private List<QuestionOptionDto.Response> options;

        /**
         * 엔티티에서 DTO 생성
         */
        public static Response from(Question question) {
            List<QuestionOptionDto.Response> optionResponses = new ArrayList<>();

            if (question.getOptions() != null) {
                optionResponses = question.getOptions().stream()
                        .map(QuestionOptionDto.Response::from)
                        .collect(Collectors.toList());
            }

            return Response.builder()
                    .id(question.getId())
                    .questionType(question.getQuestionType())
                    .content(question.getContent())
                    .required(question.getRequired())
                    .questionOrder(question.getQuestionOrder())
                    .placeholder(question.getPlaceholder())
                    .helpText(question.getHelpText())
                    .options(optionResponses)
                    .build();
        }
    }
}