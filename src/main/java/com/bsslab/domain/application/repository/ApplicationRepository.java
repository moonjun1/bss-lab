package com.bsslab.domain.application.repository;

import com.bsslab.domain.application.entity.Application;
import com.bsslab.domain.application.entity.ApplicationForm;
import com.bsslab.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 지원서(Application) 엔티티에 대한 데이터 접근 인터페이스
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    /**
     * 지원 양식에 제출된 모든 지원서 조회
     */
    Page<Application> findByApplicationForm(ApplicationForm applicationForm, Pageable pageable);

    /**
     * 지원 양식 ID로 지원서 조회
     */
    Page<Application> findByApplicationFormId(Long applicationFormId, Pageable pageable);

    /**
     * 지원서 상태별 조회
     */
    Page<Application> findByStatus(Application.Status status, Pageable pageable);

    /**
     * 지원 양식과 상태로 지원서 조회
     */
    Page<Application> findByApplicationFormIdAndStatus(Long applicationFormId, Application.Status status, Pageable pageable);

    /**
     * 사용자가 제출한 지원서 조회
     */
    Page<Application> findByUser(User user, Pageable pageable);

    /**
     * 사용자 ID로 지원서 조회
     */
    Page<Application> findByUserId(Long userId, Pageable pageable);

    /**
     * 비로그인 사용자 이메일로 지원서 조회
     */
    Page<Application> findByApplicantEmail(String email, Pageable pageable);

    /**
     * 확인 코드로 비로그인 지원서 조회
     */
    Optional<Application> findByIdAndApplicantEmail(Long id, String email);

    /**
     * 지원 양식 ID와 사용자 ID로 지원서 조회
     * (한 사용자가 같은 양식에 중복 지원 여부 확인용)
     */
    Optional<Application> findByApplicationFormIdAndUserId(Long applicationFormId, Long userId);

    /**
     * 지원 양식 ID와 이메일로 지원서 조회
     * (비로그인 사용자의 같은 양식에 중복 지원 여부 확인용)
     */
    Optional<Application> findByApplicationFormIdAndApplicantEmail(Long applicationFormId, String email);

    /**
     * 지원 양식별 지원서 개수 조회
     */
    long countByApplicationFormId(Long applicationFormId);

    /**
     * 지원 양식별, 상태별 지원서 개수 조회
     */
    long countByApplicationFormIdAndStatus(Long applicationFormId, Application.Status status);
}