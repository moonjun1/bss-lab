package com.bsslab.domain.application.service;

import com.bsslab.domain.application.dto.ApplicationFormDto;
import com.bsslab.domain.application.dto.QuestionDto;
import com.bsslab.domain.application.dto.QuestionOptionDto;
import com.bsslab.domain.application.entity.ApplicationForm;
import com.bsslab.domain.application.entity.Question;
import com.bsslab.domain.application.entity.QuestionOption;
import com.bsslab.domain.application.repository.ApplicationFormRepository;
import com.bsslab.domain.application.repository.QuestionOptionRepository;
import com.bsslab.domain.application.repository.QuestionRepository;
import com.bsslab.global.exception.GlobalExceptionHandler.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 지원 양식(ApplicationForm) 관련 비즈니스 로직 서비스
 */
@Service
@RequiredArgsConstructor
public class ApplicationFormService {

    private final ApplicationFormRepository applicationFormRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    /**
     * 활성화된 지원 양식 목록 조회 (사용자용)
     */
    public List<ApplicationFormDto.ListResponse> getActiveApplicationForms() {
        LocalDateTime now = LocalDateTime.now();
        List<ApplicationForm> activeForms = applicationFormRepository.findByStatusAndStartDateBeforeAndEndDateAfter(
                ApplicationForm.Status.PUBLISHED, now, now);

        return activeForms.stream()
                .map(ApplicationFormDto.ListResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 모든 지원 양식 목록 조회 (관리자용)
     */
    public Page<ApplicationFormDto.ListResponse> getAllApplicationForms(Pageable pageable) {
        Page<ApplicationForm> forms = applicationFormRepository.findAll(pageable);
        return forms.map(ApplicationFormDto.ListResponse::from);
    }

    /**
     * 상태별 지원 양식 목록 조회 (관리자용)
     */
    public Page<ApplicationFormDto.ListResponse> getApplicationFormsByStatus(ApplicationForm.Status status, Pageable pageable) {
        Page<ApplicationForm> forms = applicationFormRepository.findByStatus(status, pageable);
        return forms.map(ApplicationFormDto.ListResponse::from);
    }

    /**
     * 지원 양식 상세 조회
     */
    public ApplicationFormDto.DetailResponse getApplicationForm(Long id) {
        ApplicationForm form = applicationFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationForm not found with id: " + id));

        return ApplicationFormDto.DetailResponse.from(form);
    }

    /**
     * 지원 양식 생성
     */
    @Transactional
    public Long createApplicationForm(ApplicationFormDto.CreateRequest request) {
        // 지원 양식 생성
        ApplicationForm form = ApplicationForm.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : ApplicationForm.Status.DRAFT)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        ApplicationForm savedForm = applicationFormRepository.save(form);

        // 질문 생성
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            for (int i = 0; i < request.getQuestions().size(); i++) {
                QuestionDto.CreateRequest questionRequest = request.getQuestions().get(i);
                createQuestion(savedForm, questionRequest, i + 1);
            }
        }

        return savedForm.getId();
    }

    /**
     * 지원 양식 수정
     */
    @Transactional
    public void updateApplicationForm(Long id, ApplicationFormDto.UpdateRequest request) {
        ApplicationForm form = applicationFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationForm not found with id: " + id));

        form.setTitle(request.getTitle());
        form.setDescription(request.getDescription());
        form.setStatus(request.getStatus());
        form.setStartDate(request.getStartDate());
        form.setEndDate(request.getEndDate());

        applicationFormRepository.save(form);
    }

    /**
     * 지원 양식 삭제
     */
    @Transactional
    public void deleteApplicationForm(Long id) {
        ApplicationForm form = applicationFormRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationForm not found with id: " + id));

        applicationFormRepository.delete(form);
    }

    /**
     * 지원 양식에 질문 추가
     */
    @Transactional
    public Long addQuestion(Long formId, QuestionDto.CreateRequest request) {
        ApplicationForm form = applicationFormRepository.findById(formId)
                .orElseThrow(() -> new ResourceNotFoundException("ApplicationForm not found with id: " + formId));

        // 현재 질문 수 조회하여 새 질문의 순서 결정
        int order = (int) questionRepository.countByApplicationFormId(formId) + 1;

        return createQuestion(form, request, order);
    }

    /**
     * 질문 수정
     */
    @Transactional
    public void updateQuestion(Long questionId, QuestionDto.UpdateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        if (request.getQuestionType() != null) {
            question.setQuestionType(request.getQuestionType());
        }

        if (request.getContent() != null) {
            question.setContent(request.getContent());
        }

        if (request.getRequired() != null) {
            question.setRequired(request.getRequired());
        }

        if (request.getQuestionOrder() != null) {
            question.setQuestionOrder(request.getQuestionOrder());
        }

        if (request.getPlaceholder() != null) {
            question.setPlaceholder(request.getPlaceholder());
        }

        if (request.getHelpText() != null) {
            question.setHelpText(request.getHelpText());
        }

        questionRepository.save(question);
    }

    /**
     * 질문 삭제
     */
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        questionRepository.delete(question);

        // 나머지 질문들의 순서 재조정
        List<Question> remainingQuestions = questionRepository.findByApplicationFormIdOrderByQuestionOrderAsc(
                question.getApplicationForm().getId());

        for (int i = 0; i < remainingQuestions.size(); i++) {
            Question q = remainingQuestions.get(i);
            q.setQuestionOrder(i + 1);
            questionRepository.save(q);
        }
    }

    /**
     * 질문에 옵션 추가
     */
    @Transactional
    public Long addQuestionOption(Long questionId, QuestionOptionDto.CreateRequest request) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + questionId));

        // 객관식 질문인지 확인
        if (question.getQuestionType() != Question.QuestionType.SINGLE_CHOICE
                && question.getQuestionType() != Question.QuestionType.MULTIPLE_CHOICE
                && question.getQuestionType() != Question.QuestionType.DROPDOWN) {
            throw new IllegalArgumentException("Options can only be added to choice type questions");
        }

        // 현재 옵션 수 조회하여 새 옵션의 순서 결정
        int order = (int) questionOptionRepository.countByQuestionId(questionId) + 1;

        QuestionOption option = QuestionOption.builder()
                .question(question)
                .content(request.getContent())
                .optionOrder(request.getOptionOrder() != null ? request.getOptionOrder() : order)
                .build();

        question.addOption(option);
        QuestionOption savedOption = questionOptionRepository.save(option);

        return savedOption.getId();
    }

    /**
     * 질문 옵션 수정
     */
    @Transactional
    public void updateQuestionOption(Long optionId, QuestionOptionDto.UpdateRequest request) {
        QuestionOption option = questionOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption not found with id: " + optionId));

        if (request.getContent() != null) {
            option.setContent(request.getContent());
        }

        if (request.getOptionOrder() != null) {
            option.setOptionOrder(request.getOptionOrder());
        }

        questionOptionRepository.save(option);
    }

    /**
     * 질문 옵션 삭제
     */
    @Transactional
    public void deleteQuestionOption(Long optionId) {
        QuestionOption option = questionOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption not found with id: " + optionId));

        Long questionId = option.getQuestion().getId();
        questionOptionRepository.delete(option);

        // 나머지 옵션들의 순서 재조정
        List<QuestionOption> remainingOptions = questionOptionRepository.findByQuestionIdOrderByOptionOrderAsc(questionId);

        for (int i = 0; i < remainingOptions.size(); i++) {
            QuestionOption opt = remainingOptions.get(i);
            opt.setOptionOrder(i + 1);
            questionOptionRepository.save(opt);
        }
    }

    /**
     * 질문 생성 헬퍼 메소드
     */
    private Long createQuestion(ApplicationForm form, QuestionDto.CreateRequest request, int defaultOrder) {
        Question question = Question.builder()
                .applicationForm(form)
                .questionType(request.getQuestionType())
                .content(request.getContent())
                .required(request.getRequired() != null ? request.getRequired() : false)
                .questionOrder(request.getQuestionOrder() != null ? request.getQuestionOrder() : defaultOrder)
                .placeholder(request.getPlaceholder())
                .helpText(request.getHelpText())
                .build();

        form.addQuestion(question);
        Question savedQuestion = questionRepository.save(question);

        // 객관식 질문이면 옵션 추가
        if ((request.getQuestionType() == Question.QuestionType.SINGLE_CHOICE
                || request.getQuestionType() == Question.QuestionType.MULTIPLE_CHOICE
                || request.getQuestionType() == Question.QuestionType.DROPDOWN)
                && request.getOptions() != null && !request.getOptions().isEmpty()) {

            for (int i = 0; i < request.getOptions().size(); i++) {
                QuestionOptionDto.CreateRequest optionRequest = request.getOptions().get(i);

                QuestionOption option = QuestionOption.builder()
                        .question(savedQuestion)
                        .content(optionRequest.getContent())
                        .optionOrder(optionRequest.getOptionOrder() != null ? optionRequest.getOptionOrder() : i + 1)
                        .build();

                savedQuestion.addOption(option);
                questionOptionRepository.save(option);
            }
        }

        return savedQuestion.getId();
    }
}