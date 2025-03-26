package com.bsslab.domain.application.entity;

import com.bsslab.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 지원 양식 엔티티
 *
 * 관리자가 만든 지원 양식을 나타내는 엔티티 클래스입니다.
 * 하나의 지원 양식은 여러 개의 질문(Question)을 포함할 수 있습니다.
 */
@Entity
@Table(name = "application_forms")
@Getter
@Setter
@NoArgsConstructor
public class ApplicationForm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 지원 양식 제목
     */
    @Column(nullable = false)
    private String title;

    /**
     * 지원 양식 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 지원 양식 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /**
     * 지원 시작일시
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * 지원 마감일시
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * 지원 양식에 포함된 질문 목록
     */
    @OneToMany(mappedBy = "applicationForm", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("questionOrder ASC")
    private List<Question> questions = new ArrayList<>();

    /**
     * 이 지원 양식으로 제출된 지원서 목록
     */
    @OneToMany(mappedBy = "applicationForm", cascade = CascadeType.ALL)
    private List<Application> applications = new ArrayList<>();

    @Builder
    public ApplicationForm(String title, String description, Status status,
                           LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.description = description;
        this.status = status != null ? status : Status.DRAFT;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 질문 추가
     *
     * @param question 추가할 질문
     */
    public void addQuestion(Question question) {
        questions.add(question);
        question.setApplicationForm(this);
    }

    /**
     * 질문 제거
     *
     * @param question 제거할 질문
     */
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setApplicationForm(null);
    }

    /**
     * 지원 양식 상태
     */
    public enum Status {
        /** 초안 상태 (작성 중) */
        DRAFT,
        /** 게시됨 (지원 가능) */
        PUBLISHED,
        /** 마감됨 (지원 불가) */
        CLOSED
    }
}