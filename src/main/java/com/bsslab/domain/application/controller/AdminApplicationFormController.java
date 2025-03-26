package com.bsslab.domain.application.controller;

import com.bsslab.domain.application.dto.ApplicationFormDto;
import com.bsslab.domain.application.dto.QuestionDto;
import com.bsslab.domain.application.dto.QuestionOptionDto;
import com.bsslab.domain.application.entity.ApplicationForm;
import com.bsslab.domain.application.service.ApplicationFormService;
import com.bsslab.global.dto.ApiResponse;
import com.bsslab.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 지원 양식 관리 API 컨트롤러 (관리자용)
 */
@RestController
@RequestMapping("/admin/forms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "지원 양식 관리", description = "지원 양식 관리 API (관리자용)")
public class AdminApplicationFormController {

    private final ApplicationFormService applicationFormService;

    /**
     * 모든 지원 양식 목록 조회
     */
    @Operation(summary = "지원 양식 목록 조회", description = "모든 지원 양식 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApplicationFormDto.ListResponse>>> getAllApplicationForms(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApplicationFormDto.ListResponse> forms = applicationFormService.getAllApplicationForms(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(forms)));
    }

    /**
     * 상태별 지원 양식 목록 조회
     */
    @Operation(summary = "상태별 지원 양식 목록 조회", description = "특정 상태(DRAFT, PUBLISHED, CLOSED)의 지원 양식 목록을 조회합니다.")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationFormDto.ListResponse>>> getApplicationFormsByStatus(
            @PathVariable ApplicationForm.Status status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApplicationFormDto.ListResponse> forms = applicationFormService.getApplicationFormsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(forms)));
    }

    /**
     * 지원 양식 상세 조회
     */
    @Operation(summary = "지원 양식 상세 조회", description = "지원 양식의 상세 정보와 포함된 질문 목록을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationFormDto.DetailResponse>> getApplicationForm(@PathVariable Long id) {
        ApplicationFormDto.DetailResponse form = applicationFormService.getApplicationForm(id);
        return ResponseEntity.ok(ApiResponse.success(form));
    }

    /**
     * 지원 양식 생성
     */
    @Operation(summary = "지원 양식 생성",
            description = "새로운 지원 양식을 생성합니다. 질문도 함께 생성할 수 있습니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createApplicationForm(
            @Valid @RequestBody ApplicationFormDto.CreateRequest request) {
        Long formId = applicationFormService.createApplicationForm(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("지원 양식이 성공적으로 생성되었습니다.", formId));
    }

    /**
     * 지원 양식 수정
     */
    @Operation(summary = "지원 양식 수정",
            description = "기존 지원 양식의 정보를 수정합니다. 질문은 별도 API로 관리합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateApplicationForm(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationFormDto.UpdateRequest request) {
        applicationFormService.updateApplicationForm(id, request);
        return ResponseEntity.ok(ApiResponse.success("지원 양식이 성공적으로 수정되었습니다.", null));
    }

    /**
     * 지원 양식 삭제
     */
    @Operation(summary = "지원 양식 삭제", description = "지원 양식을 삭제합니다. 포함된 질문과 옵션도 함께 삭제됩니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplicationForm(@PathVariable Long id) {
        applicationFormService.deleteApplicationForm(id);
        return ResponseEntity.ok(ApiResponse.success("지원 양식이 성공적으로 삭제되었습니다.", null));
    }

    /**
     * 질문 추가
     */
    @Operation(summary = "질문 추가", description = "지원 양식에 새로운 질문을 추가합니다.")
    @PostMapping("/{formId}/questions")
    public ResponseEntity<ApiResponse<Long>> addQuestion(
            @PathVariable Long formId,
            @Valid @RequestBody QuestionDto.CreateRequest request) {
        Long questionId = applicationFormService.addQuestion(formId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("질문이 성공적으로 추가되었습니다.", questionId));
    }

    /**
     * 질문 수정
     */
    @Operation(summary = "질문 수정", description = "기존 질문의 정보를 수정합니다.")
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionDto.UpdateRequest request) {
        applicationFormService.updateQuestion(questionId, request);
        return ResponseEntity.ok(ApiResponse.success("질문이 성공적으로 수정되었습니다.", null));
    }

    /**
     * 질문 삭제
     */
    @Operation(summary = "질문 삭제", description = "질문을 삭제합니다. 포함된 옵션도 함께 삭제됩니다.")
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long questionId) {
        applicationFormService.deleteQuestion(questionId);
        return ResponseEntity.ok(ApiResponse.success("질문이 성공적으로 삭제되었습니다.", null));
    }

    /**
     * 질문 옵션 추가
     */
    @Operation(summary = "질문 옵션 추가",
            description = "객관식 질문(SINGLE_CHOICE, MULTIPLE_CHOICE, DROPDOWN)에 새로운 옵션을 추가합니다.")
    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<ApiResponse<Long>> addQuestionOption(
            @PathVariable Long questionId,
            @Valid @RequestBody QuestionOptionDto.CreateRequest request) {
        Long optionId = applicationFormService.addQuestionOption(questionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("질문 옵션이 성공적으로 추가되었습니다.", optionId));
    }

    /**
     * 질문 옵션 수정
     */
    @Operation(summary = "질문 옵션 수정", description = "기존 질문 옵션의 정보를 수정합니다.")
    @PutMapping("/options/{optionId}")
    public ResponseEntity<ApiResponse<Void>> updateQuestionOption(
            @PathVariable Long optionId,
            @Valid @RequestBody QuestionOptionDto.UpdateRequest request) {
        applicationFormService.updateQuestionOption(optionId, request);
        return ResponseEntity.ok(ApiResponse.success("질문 옵션이 성공적으로 수정되었습니다.", null));
    }

    /**
     * 질문 옵션 삭제
     */
    @Operation(summary = "질문 옵션 삭제", description = "질문 옵션을 삭제합니다.")
    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestionOption(@PathVariable Long optionId) {
        applicationFormService.deleteQuestionOption(optionId);
        return ResponseEntity.ok(ApiResponse.success("질문 옵션이 성공적으로 삭제되었습니다.", null));
    }
}