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
 * 질문 옵션 엔티티
 *
 * 객관식 질문(SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN)에 사용되는
 * 선택 옵션을 나타내는 엔티티 클래스입니다.
 */
@Entity
@Table(name = "question_options")
@Getter
@Setter
@NoArgsConstructor
public class QuestionOption extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이 옵션이 속한 질문
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    /**
     * 옵션 내용
     */
    @Column(nullable = false)
    private String content;

    /**
     * 옵션 순서
     */
    @Column(name = "option_order", nullable = false)
    private Integer optionOrder;

    /**
     * 이 옵션을 선택한 답변 목록
     */
    @ManyToMany(mappedBy = "selectedOptions")
    private Set<ApplicationAnswer> answers = new HashSet<>();

    @Builder
    public QuestionOption(Question question, String content, Integer optionOrder) {
        this.question = question;
        this.content = content;
        this.optionOrder = optionOrder;
    }
}