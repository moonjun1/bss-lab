package com.bsslab.domain.application.repository;

import com.bsslab.domain.application.entity.ApplicationForm;
import com.bsslab.domain.application.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 질문(Question) 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    /**
     * 지원 양식에 속한 질문 목록 조회
     */
    List<Question> findByApplicationFormOrderByQuestionOrderAsc(ApplicationForm applicationForm);

    /**
     * 지원 양식 ID로 질문 목록 조회
     */
    List<Question> findByApplicationFormIdOrderByQuestionOrderAsc(Long applicationFormId);

    /**
     * 지원 양식에 속한 질문 개수 조회
     */
    long countByApplicationFormId(Long applicationFormId);

    /**
     * 지원 양식에 속한 질문 삭제
     */
    void deleteByApplicationFormId(Long applicationFormId);
}