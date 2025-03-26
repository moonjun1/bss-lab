package com.bsslab.domain.application.repository;

import com.bsslab.domain.application.entity.ApplicationForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 지원 양식(ApplicationForm) 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface ApplicationFormRepository extends JpaRepository<ApplicationForm, Long> {

    /**
     * 상태별 지원 양식 조회
     */
    Page<ApplicationForm> findByStatus(ApplicationForm.Status status, Pageable pageable);

    /**
     * 제목으로 지원 양식 검색
     */
    Page<ApplicationForm> findByTitleContaining(String title, Pageable pageable);

    /**
     * 현재 활성화된(PUBLISHED 상태이며 기간 내인) 지원 양식 조회
     */
    List<ApplicationForm> findByStatusAndStartDateBeforeAndEndDateAfter(
            ApplicationForm.Status status,
            LocalDateTime currentTime1,
            LocalDateTime currentTime2);

    /**
     * 현재 활성화된 지원 양식 개수 조회
     */
    long countByStatusAndStartDateBeforeAndEndDateAfter(
            ApplicationForm.Status status,
            LocalDateTime currentTime1,
            LocalDateTime currentTime2);
}