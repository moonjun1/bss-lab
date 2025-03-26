package com.bsslab.domain.application.service;

import com.bsslab.domain.application.dto.ApplicationAnswerDto;
import com.bsslab.domain.application.dto.ApplicationDto;
import com.bsslab.domain.application.entity.Application;
import com.bsslab.domain.application.entity.ApplicationAnswer;
import com.bsslab.domain.application.entity.ApplicationForm;
import com.bsslab.domain.application.entity.Question;
import com.bsslab.domain.application.entity.QuestionOption;
import com.bsslab.domain.application.repository.ApplicationAnswerRepository;
import com.bsslab.domain.application.repository.ApplicationFormRepository;
import com.bsslab.domain.application.repository.ApplicationRepository;
import com.bsslab.domain.application.repository.QuestionOptionRepository;
import com.bsslab.domain.application.repository.QuestionRepository;
import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.repository.UserRepository;
import com.bsslab.global.exception.GlobalExceptionHandler.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 지원서(Application) 관련 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationFormRepository applicationFormRepository;
    private final ApplicationAnswerRepository applicationAnswerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final UserRepository userRepository;

    /**
     * 지원서 목록 조회 (관리자용)
     */
    public Page<ApplicationDto.ListResponse> getAllApplications(Pageable pageable) {
        Page<Application> applications = applicationRepository.findAll(pageable);
        return applications.map(ApplicationDto.ListResponse::from);
    }

    /**
     * 지원 양식별 지원서 목록 조회 (관리자용)
     */
    public Page<ApplicationDto.ListResponse> getApplicationsByForm(Long formId, Pageable pageable) {
        Page<Application> applications = applicationRepository.findByApplicationFormId(formId, pageable);
        return applications.map(ApplicationDto.ListResponse::from);
    }

    /**
     * 상태별 지원서 목록 조회 (관리자용)
     */
    public Page<ApplicationDto.ListResponse> getApplicationsByStatus(Application.Status status, Pageable pageable) {
        Page<Application> applications = applicationRepository.findByStatus(status, pageable);
        return applications.map(ApplicationDto.ListResponse::from);
    }

    /**
     * 사용자별 지원서 목록 조회
     */
    public Page<ApplicationDto.ListResponse> getApplicationsByUser(Long userId, Pageable pageable) {
        Page<Application> applications = applicationRepository.findByUserId(userId, pageable);
        return applications.map(ApplicationDto.ListResponse::from);
    }

    /**
     * 이메일로 지원서 목록 조회 (비로그인 사용자용)
     */
    public Page<ApplicationDto.ListResponse> getApplicationsByEmail(String email, Pageable pageable) {
        Page<Application> applications = applicationRepository.findByApplicantEmail(email, pageable);
        return applications.map(ApplicationDto.ListResponse::from);
    }

    /**
     * 지원서 상세 조회
     */
    public ApplicationDto.DetailResponse getApplication(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        return ApplicationDto.DetailResponse.from(application);
    }

    /**
     * 이메일로 지원서 상세 조회 (비로그인 사용자용)
     */
    public ApplicationDto.DetailResponse getApplicationByIdAndEmail(Long id, String email) {
        Application application = applicationRepository.findByIdAndApplicantEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found or email doesn't match"));

        return ApplicationDto.DetailResponse.from(application);
    }

    /**
     * 지원서 생성/저장
     */
    @Transactional
    public Long createApplication(ApplicationDto.CreateRequest request, User user) {
        // 지원 양식 조회
        ApplicationForm form = applicationFormRepository.findById(request.getApplicationFormId())
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationForm not found with id: " + request.getApplicationFormId()));

        // 지원 양식이 PUBLISHED 상태인지 확인
        if (form.getStatus() != ApplicationForm.Status.PUBLISHED) {
            throw new IllegalStateException("ApplicationForm is not open for applications");
        }

        // 지원 기간 확인
        LocalDateTime now = LocalDateTime.now();
        if (form.getStartDate() != null && now.isBefore(form.getStartDate())) {
            throw new IllegalStateException("Application period has not started yet");
        }
        if (form.getEndDate() != null && now.isAfter(form.getEndDate())) {
            throw new IllegalStateException("Application period has ended");
        }

        // 중복 지원 체크 (로그인 사용자)
        if (user != null) {
            Optional<Application> existingApplication = applicationRepository.findByApplicationFormIdAndUserId(
                    form.getId(), user.getId());

            if (existingApplication.isPresent() &&
                    (existingApplication.get().getStatus() == Application.Status.SUBMITTED ||
                            existingApplication.get().getStatus() == Application.Status.UNDER_REVIEW ||
                            existingApplication.get().getStatus() == Application.Status.ACCEPTED)) {
                throw new IllegalStateException("You have already submitted an application for this form");
            }
        }
        // 중복 지원 체크 (비로그인 사용자)
        else {
            Optional<Application> existingApplication = applicationRepository.findByApplicationFormIdAndApplicantEmail(
                    form.getId(), request.getApplicantEmail());

            if (existingApplication.isPresent() &&
                    (existingApplication.get().getStatus() == Application.Status.SUBMITTED ||
                            existingApplication.get().getStatus() == Application.Status.UNDER_REVIEW ||
                            existingApplication.get().getStatus() == Application.Status.ACCEPTED)) {
                throw new IllegalStateException("An application with this email has already been submitted for this form");
            }
        }

        // 지원서 생성
        Application application = Application.builder()
                .applicationForm(form)
                .user(user)
                .applicantName(request.getApplicantName())
                .applicantEmail(request.getApplicantEmail())
                .applicantPhone(request.getApplicantPhone())
                .status(request.getStatus() != null ? request.getStatus() : Application.Status.DRAFT)
                .build();

        // 제출 상태라면 제출 시간 기록
        if (application.getStatus() == Application.Status.SUBMITTED) {
            application.setSubmittedAt(LocalDateTime.now());
        }

        Application savedApplication = applicationRepository.save(application);

        // 답변 생성
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            saveAnswers(savedApplication, request.getAnswers());
        }

        return savedApplication.getId();
    }

    /**
     * 지원서 수정
     */
    @Transactional
    public void updateApplication(Long id, ApplicationDto.UpdateRequest request, User user) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        // 권한 체크 (로그인 사용자)
        if (user != null) {
            if (application.getUser() != null && !application.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You don't have permission to update this application");
            }
        }
        // 비로그인 사용자는 이메일로 확인
        else {
            if (!application.getApplicantEmail().equals(request.getApplicantEmail())) {
                throw new AccessDeniedException("Email doesn't match the application");
            }
        }

        // 이미 제출된 지원서는 수정 불가 (DRAFT 상태만 수정 가능)
        if (application.getStatus() != Application.Status.DRAFT) {
            throw new IllegalStateException("Only draft applications can be updated");
        }

        // 지원서 정보 수정
        if (request.getApplicantName() != null) {
            application.setApplicantName(request.getApplicantName());
        }

        if (request.getApplicantEmail() != null) {
            application.setApplicantEmail(request.getApplicantEmail());
        }

        if (request.getApplicantPhone() != null) {
            application.setApplicantPhone(request.getApplicantPhone());
        }

        // 상태 변경 (DRAFT -> SUBMITTED)
        if (request.getStatus() != null && request.getStatus() == Application.Status.SUBMITTED) {
            application.setStatus(Application.Status.SUBMITTED);
            application.setSubmittedAt(LocalDateTime.now());
        }

        applicationRepository.save(application);

        // 답변 수정
        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            updateAnswers(application, request.getAnswers());
        }
    }

    /**
     * 지원서 상태 변경 (관리자용)
     */
    @Transactional
    public void updateApplicationStatus(Long id, ApplicationDto.StatusUpdateRequest request) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        // 상태 변경
        application.setStatus(request.getStatus());

        // 검토 관련 상태라면 검토 시간 기록
        if (request.getStatus() == Application.Status.UNDER_REVIEW ||
                request.getStatus() == Application.Status.ACCEPTED ||
                request.getStatus() == Application.Status.REJECTED) {
            application.setReviewedAt(LocalDateTime.now());
        }

        // 검토자 코멘트 추가
        if (request.getReviewerComment() != null) {
            application.setReviewerComment(request.getReviewerComment());
        }

        applicationRepository.save(application);
    }

    /**
     * 지원서 삭제
     */
    @Transactional
    public void deleteApplication(Long id, User user) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        // 권한 체크 (로그인 사용자)
        if (user != null) {
            if (application.getUser() != null && !application.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You don't have permission to delete this application");
            }
        }

        // 이미 제출된 지원서는 삭제 불가 (DRAFT 상태만 삭제 가능, 아니면 관리자만 가능)
        if (application.getStatus() != Application.Status.DRAFT) {
            // 관리자 체크 필요
            throw new IllegalStateException("Only draft applications can be deleted");
        }

        applicationRepository.delete(application);
    }

    /**
     * 답변 저장 헬퍼 메소드
     */
    private void saveAnswers(Application application, List<ApplicationAnswerDto.CreateRequest> answerRequests) {
        for (ApplicationAnswerDto.CreateRequest answerRequest : answerRequests) {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + answerRequest.getQuestionId()));

            // 이 질문이 지원 양식에 속하는지 확인
            if (!question.getApplicationForm().getId().equals(application.getApplicationForm().getId())) {
                throw new IllegalArgumentException("Question does not belong to the application form");
            }

            ApplicationAnswer answer = ApplicationAnswer.builder()
                    .application(application)
                    .question(question)
                    .textValue(answerRequest.getTextValue())
                    .build();

            application.addAnswer(answer);
            ApplicationAnswer savedAnswer = applicationAnswerRepository.save(answer);

            // 객관식 답변이면 선택된 옵션 저장
            if ((question.getQuestionType() == Question.QuestionType.SINGLE_CHOICE
                    || question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE
                    || question.getQuestionType() == Question.QuestionType.DROPDOWN)
                    && answerRequest.getSelectedOptionIds() != null && !answerRequest.getSelectedOptionIds().isEmpty()) {

                List<QuestionOption> selectedOptions = questionOptionRepository.findByIdIn(answerRequest.getSelectedOptionIds());

                // 이 옵션들이 질문에 속하는지 확인
                for (QuestionOption option : selectedOptions) {
                    if (!option.getQuestion().getId().equals(question.getId())) {
                        throw new IllegalArgumentException("Option does not belong to the question");
                    }

                    savedAnswer.addSelectedOption(option);
                }

                applicationAnswerRepository.save(savedAnswer);
            }
        }
    }

    /**
     * 답변 수정 헬퍼 메소드
     */
    private void updateAnswers(Application application, List<ApplicationAnswerDto.UpdateRequest> answerRequests) {
        for (ApplicationAnswerDto.UpdateRequest answerRequest : answerRequests) {
            Question question = questionRepository.findById(answerRequest.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + answerRequest.getQuestionId()));

            // 이 질문이 지원 양식에 속하는지 확인
            if (!question.getApplicationForm().getId().equals(application.getApplicationForm().getId())) {
                throw new IllegalArgumentException("Question does not belong to the application form");
            }

            // 기존 답변이 있는지 확인
            Optional<ApplicationAnswer> existingAnswerOpt = applicationAnswerRepository.findByApplicationIdAndQuestionId(
                    application.getId(), question.getId());

            ApplicationAnswer answer;

            if (existingAnswerOpt.isPresent()) {
                // 기존 답변 수정
                answer = existingAnswerOpt.get();
                answer.setTextValue(answerRequest.getTextValue());

                // 객관식 답변이면 선택된 옵션 업데이트
                if ((question.getQuestionType() == Question.QuestionType.SINGLE_CHOICE
                        || question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE
                        || question.getQuestionType() == Question.QuestionType.DROPDOWN)) {

                    // 기존 옵션 선택 제거
                    answer.getSelectedOptions().clear();

                    // 새로운 옵션 선택 추가
                    if (answerRequest.getSelectedOptionIds() != null && !answerRequest.getSelectedOptionIds().isEmpty()) {
                        List<QuestionOption> selectedOptions = questionOptionRepository.findByIdIn(answerRequest.getSelectedOptionIds());

                        // 이 옵션들이 질문에 속하는지 확인
                        for (QuestionOption option : selectedOptions) {
                            if (!option.getQuestion().getId().equals(question.getId())) {
                                throw new IllegalArgumentException("Option does not belong to the question");
                            }

                            answer.addSelectedOption(option);
                        }
                    }
                }
            } else {
                // 새 답변 생성
                answer = ApplicationAnswer.builder()
                        .application(application)
                        .question(question)
                        .textValue(answerRequest.getTextValue())
                        .build();

                application.addAnswer(answer);

                // 객관식 답변이면 선택된 옵션 저장
                if ((question.getQuestionType() == Question.QuestionType.SINGLE_CHOICE
                        || question.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE
                        || question.getQuestionType() == Question.QuestionType.DROPDOWN)
                        && answerRequest.getSelectedOptionIds() != null && !answerRequest.getSelectedOptionIds().isEmpty()) {

                    List<QuestionOption> selectedOptions = questionOptionRepository.findByIdIn(answerRequest.getSelectedOptionIds());

                    // 이 옵션들이 질문에 속하는지 확인
                    for (QuestionOption option : selectedOptions) {
                        if (!option.getQuestion().getId().equals(question.getId())) {
                            throw new IllegalArgumentException("Option does not belong to the question");
                        }

                        answer.addSelectedOption(option);
                    }
                }
            }

            applicationAnswerRepository.save(answer);
        }
    }
}