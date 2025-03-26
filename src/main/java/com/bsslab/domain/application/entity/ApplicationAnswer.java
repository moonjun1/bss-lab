package com.bsslab.domain.application.entity;

import com.bsslab.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * 지원 답변 엔티티
 *
 * 지원서에 포함된 개별 질문에 대한 사용자의 답변을 나타내는 엔티티 클래스입니다.
 * 질문 유형에 따라 텍스트 답변이나 객관식 선택 등 다양한 형태의 답변을 저장합니다.
 */
@Entity
@Table(name = "application_answers")
@Getter
@Setter
@NoArgsConstructor
public class ApplicationAnswer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이 답변이 속한 지원서
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    /**
     * 이 답변이 응답하는 질문
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * 텍스트 답변 값
     * (SHORT_TEXT, LONG_TEXT, EMAIL, PHONE, NUMBER, DATE 질문 유형에 사용)
     */
    @Column(name = "text_value", columnDefinition = "TEXT")
    private String textValue;

    /**
     * 객관식 답변에서 선택된 옵션들
     * (SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN 질문 유형에 사용)
     */
    @ManyToMany
    @JoinTable(
            name = "application_answer_options",
            joinColumns = @JoinColumn(name = "answer_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<QuestionOption> selectedOptions = new HashSet<>();

    @Builder
    public ApplicationAnswer(Application application, Question question, String textValue) {
        this.application = application;
        this.question = question;
        this.textValue = textValue;
    }

    /**
     * 객관식 답변에 옵션 추가
     *
     * @param option 선택한 옵션
     */
    public void addSelectedOption(QuestionOption option) {
        this.selectedOptions.add(option);
    }

    /**
     * 객관식 답변에서 옵션 제거
     *
     * @param option 제거할 옵션
     */
    public void removeSelectedOption(QuestionOption option) {
        this.selectedOptions.remove(option);
    }
}