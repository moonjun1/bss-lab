package com.bsslab.domain.application.entity;

import com.bsslab.common.entity.BaseTimeEntity;
import com.bsslab.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 지원서 엔티티
 *
 * 사용자가 작성하고 제출한 지원서를 나타내는 엔티티 클래스입니다.
 * 로그인한 사용자뿐만 아니라 비로그인 사용자도 이메일을 통해 지원 가능합니다.
 */
@Entity
@Table(name = "applications")
@Getter
@Setter
@NoArgsConstructor
public class Application extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 이 지원서가 제출된 지원 양식
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id", nullable = false)
    private ApplicationForm applicationForm;

    /**
     * 지원서를 제출한 사용자 (로그인한 경우)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 지원자 이름 (비로그인 지원 시 사용)
     */
    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    /**
     * 지원자 이메일 (비로그인 지원 시 사용)
     */
    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    /**
     * 지원자 전화번호 (선택사항)
     */
    @Column(name = "applicant_phone")
    private String applicantPhone;

    /**
     * 지원서 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    /**
     * 제출 일시
     */
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    /**
     * 검토 일시
     */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    /**
     * 검토자 코멘트
     */
    @Column(name = "reviewer_comment", columnDefinition = "TEXT")
    private String reviewerComment;

    /**
     * 이 지원서에 포함된 질문 답변 목록
     */
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationAnswer> answers = new ArrayList<>();

    @Builder
    public Application(ApplicationForm applicationForm, User user, String applicantName,
                       String applicantEmail, String applicantPhone, Status status,
                       LocalDateTime submittedAt, LocalDateTime reviewedAt, String reviewerComment) {
        this.applicationForm = applicationForm;
        this.user = user;
        this.applicantName = applicantName;
        this.applicantEmail = applicantEmail;
        this.applicantPhone = applicantPhone;
        this.status = status != null ? status : Status.DRAFT;
        this.submittedAt = submittedAt;
        this.reviewedAt = reviewedAt;
        this.reviewerComment = reviewerComment;
    }

    /**
     * 답변 추가
     *
     * @param answer 추가할 답변
     */
    public void addAnswer(ApplicationAnswer answer) {
        answers.add(answer);
        answer.setApplication(this);
    }

    /**
     * 지원서 상태
     */
    public enum Status {
        /** 임시저장 */
        DRAFT,
        /** 제출됨 */
        SUBMITTED,
        /** 검토 중 */
        UNDER_REVIEW,
        /** 합격 */
        ACCEPTED,
        /** 불합격 */
        REJECTED,
        /** 취소됨 */
        CANCELLED
    }
}