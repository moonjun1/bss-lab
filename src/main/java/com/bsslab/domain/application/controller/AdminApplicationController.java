package com.bsslab.domain.application.controller;

import com.bsslab.domain.application.dto.ApplicationDto;
import com.bsslab.domain.application.entity.Application;
import com.bsslab.domain.application.service.ApplicationService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 지원서 관리 API 컨트롤러 (관리자용)
 */
@RestController
@RequestMapping("/admin/applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearer-jwt")
@Tag(name = "지원서 관리", description = "지원서 관리 API (관리자용)")
public class AdminApplicationController {

    private final ApplicationService applicationService;

    /**
     * 모든 지원서 목록 조회
     */
    @Operation(summary = "지원서 목록 조회", description = "모든 지원서 목록을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ApplicationDto.ListResponse>>> getAllApplications(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApplicationDto.ListResponse> applications = applicationService.getAllApplications(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(applications)));
    }

    /**
     * 지원 양식별 지원서 목록 조회
     */
    @Operation(summary = "지원 양식별 지원서 목록 조회",
            description = "특정 지원 양식에 제출된 지원서 목록을 조회합니다.")
    @GetMapping("/form/{formId}")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationDto.ListResponse>>> getApplicationsByForm(
            @PathVariable Long formId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApplicationDto.ListResponse> applications = applicationService.getApplicationsByForm(formId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(applications)));
    }

    /**
     * 상태별 지원서 목록 조회
     */
    @Operation(summary = "상태별 지원서 목록 조회",
            description = "특정 상태(DRAFT, SUBMITTED, UNDER_REVIEW, ACCEPTED, REJECTED, CANCELLED)의 지원서 목록을 조회합니다.")
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationDto.ListResponse>>> getApplicationsByStatus(
            @PathVariable Application.Status status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApplicationDto.ListResponse> applications = applicationService.getApplicationsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(applications)));
    }

    /**
     * 지원서 상세 조회
     */
    @Operation(summary = "지원서 상세 조회",
            description = "지원서의 상세 정보와 답변 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationDto.DetailResponse>> getApplication(@PathVariable Long id) {
        ApplicationDto.DetailResponse application = applicationService.getApplication(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    /**
     * 지원서 상태 변경
     */
    @Operation(summary = "지원서 상태 변경",
            description = "지원서의 상태(UNDER_REVIEW, ACCEPTED, REJECTED, CANCELLED)를 변경합니다.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateApplicationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDto.StatusUpdateRequest request) {
        applicationService.updateApplicationStatus(id, request);

        String message;
        switch (request.getStatus()) {
            case UNDER_REVIEW:
                message = "지원서가 검토 중 상태로 변경되었습니다.";
                break;
            case ACCEPTED:
                message = "지원서가 합격 상태로 변경되었습니다.";
                break;
            case REJECTED:
                message = "지원서가 불합격 상태로 변경되었습니다.";
                break;
            case CANCELLED:
                message = "지원서가 취소 상태로 변경되었습니다.";
                break;
            default:
                message = "지원서 상태가 변경되었습니다.";
        }

        return ResponseEntity.ok(ApiResponse.success(message, null));
    }

    /**
     * 지원서 삭제
     */
    @Operation(summary = "지원서 삭제", description = "지원서를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id, null);
        return ResponseEntity.ok(ApiResponse.success("지원서가 성공적으로 삭제되었습니다.", null));
    }
}