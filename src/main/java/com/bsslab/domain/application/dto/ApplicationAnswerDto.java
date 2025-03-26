package com.bsslab.domain.application.dto;

import com.bsslab.domain.application.entity.ApplicationAnswer;
import com.bsslab.domain.application.entity.Question;
import com.bsslab.domain.application.entity.QuestionOption;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ApplicationAnswer 관련 DTO 클래스 모음
 */
public class ApplicationAnswerDto {

    /**
     * 지원 답변 생성 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 답변 생성 요청")
    public static class CreateRequest {
        @NotNull(message = "질문 ID는 필수 입력값입니다.")
        @Schema(description = "질문 ID", example = "1")
        private Long questionId;

        @Schema(description = "텍스트 답변 값", example = "안녕하세요. 저는 홍길동입니다. 인공지능 분야에 관심이 많으며...")
        private String textValue;

        @Schema(description = "선택한 옵션 ID 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN 유형의 질문인 경우)",
                example = "[1, 3]")
        private List<Long> selectedOptionIds;
    }

    /**
     * 지원 답변 수정 요청 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 답변 수정 요청")
    public static class UpdateRequest {
        @NotNull(message = "질문 ID는 필수 입력값입니다.")
        @Schema(description = "질문 ID", example = "1")
        private Long questionId;

        @Schema(description = "텍스트 답변 값", example = "안녕하세요. 저는 홍길동입니다. 인공지능과 머신러닝 분야에 관심이 많으며...")
        private String textValue;

        @Schema(description = "선택한 옵션 ID 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN 유형의 질문인 경우)",
                example = "[1, 4]")
        private List<Long> selectedOptionIds;
    }

    /**
     * 지원 답변 응답 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "지원 답변 응답")
    public static class Response {
        @Schema(description = "답변 ID", example = "1")
        private Long id;

        @Schema(description = "질문 ID", example = "1")
        private Long questionId;

        @Schema(description = "질문 내용", example = "자기소개를 해주세요.")
        private String questionContent;

        @Schema(description = "질문 유형", example = "SHORT_TEXT")
        private Question.QuestionType questionType;

        @Schema(description = "텍스트 답변 값", example = "안녕하세요. 저는 홍길동입니다. 인공지능 분야에 관심이 많으며...")
        private String textValue;

        @Schema(description = "선택한 옵션 목록 (SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN 유형의 질문인 경우)")
        private List<QuestionOptionDto.Response> selectedOptions;

        /**
         * 엔티티에서 DTO 생성
         */
        public static Response from(ApplicationAnswer answer) {
            List<QuestionOptionDto.Response> selectedOptionResponses = answer.getSelectedOptions().stream()
                    .map(QuestionOptionDto.Response::from)
                    .collect(Collectors.toList());

            return Response.builder()
                    .id(answer.getId())
                    .questionId(answer.getQuestion().getId())
                    .questionContent(answer.getQuestion().getContent())
                    .questionType(answer.getQuestion().getQuestionType())
                    .textValue(answer.getTextValue())
                    .selectedOptions(selectedOptionResponses)
                    .build();
        }
    }
}