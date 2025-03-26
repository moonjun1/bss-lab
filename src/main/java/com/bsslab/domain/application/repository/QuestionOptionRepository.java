package com.bsslab.domain.application.repository;

import com.bsslab.domain.application.entity.Question;
import com.bsslab.domain.application.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 질문 옵션(QuestionOption) 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {

    /**
     * 질문에 속한 옵션 목록 조회
     */
    List<QuestionOption> findByQuestionOrderByOptionOrderAsc(Question question);

    /**
     * 질문 ID로 옵션 목록 조회
     */
    List<QuestionOption> findByQuestionIdOrderByOptionOrderAsc(Long questionId);

    /**
     * 질문에 속한 옵션 개수 조회
     */
    long countByQuestionId(Long questionId);

    /**
     * 질문에 속한 옵션 삭제
     */
    void deleteByQuestionId(Long questionId);

    /**
     * 옵션 ID 목록으로 옵션 조회
     */
    List<QuestionOption> findByIdIn(List<Long> ids);
}