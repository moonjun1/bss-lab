package com.bsslab.domain.application.repository;

import com.bsslab.domain.application.entity.Application;
import com.bsslab.domain.application.entity.ApplicationAnswer;
import com.bsslab.domain.application.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 지원 답변(ApplicationAnswer) 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface ApplicationAnswerRepository extends JpaRepository<ApplicationAnswer, Long> {

    /**
     * 지원서에 포함된 모든 답변 조회
     */
    List<ApplicationAnswer> findByApplication(Application application);

    /**
     * 지원서 ID로 답변 목록 조회
     */
    List<ApplicationAnswer> findByApplicationId(Long applicationId);

    /**
     * 특정 질문에 대한 답변 조회
     */
    List<ApplicationAnswer> findByQuestion(Question question);

    /**
     * 질문 ID로 답변 목록 조회
     */
    List<ApplicationAnswer> findByQuestionId(Long questionId);

    /**
     * 지원서와 질문으로 답변 조회
     */
    Optional<ApplicationAnswer> findByApplicationAndQuestion(Application application, Question question);

    /**
     * 지원서 ID와 질문 ID로 답변 조회
     */
    Optional<ApplicationAnswer> findByApplicationIdAndQuestionId(Long applicationId, Long questionId);

    /**
     * 지원서에 속한 답변 개수 조회
     */
    long countByApplicationId(Long applicationId);

    /**
     * 지원서에 속한 답변 삭제
     */
    void deleteByApplicationId(Long applicationId);
}