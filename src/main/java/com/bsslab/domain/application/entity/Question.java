package com.bsslab.domain.application.entity;

import com.bsslab.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 질문 엔티티
 *
 * 지원 양식에 포함되는 개별 질문을 나타내는 엔티티 클래스입니다.
 * 질문 유형에 따라 텍스트 입력, 객관식 선택 등 다양한 형태의 답변을 받을 수 있습니다.
 */
@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이 질문이 속한 지원 양식
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id", nullable = false)
    private ApplicationForm applicationForm;

    /**
     * 질문 유형 (텍스트, 객관식 등)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    /**
     * 질문 내용
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 필수 응답 여부
     */
    @Column(nullable = false)
    private Boolean required;

    /**
     * 질문 순서
     */
    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;

    /**
     * 입력란 안내 텍스트
     */
    @Column(name = "placeholder")
    private String placeholder;

    /**
     * 질문에 대한 도움말
     */
    @Column(name = "help_text")
    private String helpText;

    /**
     * 객관식 질문의 선택 옵션 목록
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("optionOrder ASC")
    private List<QuestionOption> options = new ArrayList<>();

    /**
     * 이 질문에 대한 답변 목록
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<ApplicationAnswer> answers = new ArrayList<>();

    @Builder
    public Question(ApplicationForm applicationForm, QuestionType questionType, String content,
                    Boolean required, Integer questionOrder, String placeholder, String helpText) {
        this.applicationForm = applicationForm;
        this.questionType = questionType;
        this.content = content;
        this.required = required != null ? required : false;
        this.questionOrder = questionOrder;
        this.placeholder = placeholder;
        this.helpText = helpText;
    }

    /**
     * 객관식 옵션 추가
     *
     * @param option 추가할 옵션
     */
    public void addOption(QuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }

    /**
     * 객관식 옵션 제거
     *
     * @param option 제거할 옵션
     */
    public void removeOption(QuestionOption option) {
        options.remove(option);
        option.setQuestion(null);
    }

    /**
     * 질문 유형
     */
    public enum QuestionType {
        /** 짧은 텍스트 (한 줄) */
        SHORT_TEXT,
        /** 긴 텍스트 (여러 줄) */
        LONG_TEXT,
        /** 객관식 (단일 선택) */
        SINGLE_CHOICE,
        /** 객관식 (다중 선택) */
        MULTIPLE_CHOICE,
        /** 드롭다운 선택 */
        DROPDOWN,
        /** 날짜 선택 */
        DATE,
        /** 이메일 입력 */
        EMAIL,
        /** 전화번호 입력 */
        PHONE,
        /** 숫자 입력 */
        NUMBER
    }
}