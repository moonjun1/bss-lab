package com.bsslab.domain.application.controller;

import com.bsslab.domain.application.dto.ApplicationDto;
import com.bsslab.domain.application.entity.Application;
import com.bsslab.domain.application.service.ApplicationService;
import com.bsslab.global.dto.ApiResponse;
import com.bsslab.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 지원서 관련 API 컨트롤러
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "지원서", description = "지원서 제출 및 관리 API")
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * 지원서 목록 조회 (로그인 사용자)
     */
    @Operation(summary = "내 지원서 목록 조회",
            description = "로그인한 사용자의 지원서 목록을 조회합니다.")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationDto.ListResponse>>> getMyApplications(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        // UserDetails에서 사용자 ID를 추출하는 로직 필요
        Long userId = 1L; // 임시 값, 실제로는 UserDetails에서 추출

        Page<ApplicationDto.ListResponse> applications = applicationService.getApplicationsByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(applications)));
    }

    /**
     * 이메일로 지원서 목록 조회 (비로그인 사용자)
     */
    @Operation(summary = "이메일로 지원서 목록 조회",
            description = "비로그인 사용자가 이메일을 통해 자신의 지원서 목록을 조회합니다.")
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationDto.ListResponse>>> getApplicationsByEmail(
            @Parameter(description = "조회할 지원서의 이메일", required = true)
            @RequestParam String email,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ApplicationDto.ListResponse> applications = applicationService.getApplicationsByEmail(email, pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(applications)));
    }

    /**
     * 지원서 상세 조회 (로그인 사용자)
     */
    @Operation(summary = "지원서 상세 조회",
            description = "로그인한 사용자가 자신의 지원서 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<ApplicationDto.DetailResponse>> getApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ApplicationDto.DetailResponse application = applicationService.getApplication(id);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    /**
     * 이메일로 지원서 상세 조회 (비로그인 사용자)
     */
    @Operation(summary = "이메일로 지원서 상세 조회",
            description = "비로그인 사용자가 이메일을 통해 자신의 지원서 상세 정보를 조회합니다.")
    @GetMapping("/{id}/verify")
    public ResponseEntity<ApiResponse<ApplicationDto.DetailResponse>> getApplicationByIdAndEmail(
            @PathVariable Long id,
            @Parameter(description = "지원서 제출시 사용한 이메일", required = true)
            @RequestParam String email) {
        ApplicationDto.DetailResponse application = applicationService.getApplicationByIdAndEmail(id, email);
        return ResponseEntity.ok(ApiResponse.success(application));
    }

    /**
     * 지원서 생성 (로그인 사용자)
     */
    @Operation(summary = "지원서 생성 (로그인)",
            description = "로그인한 사용자가 지원서를 작성합니다.")
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Long>> createApplication(
            @Valid @RequestBody ApplicationDto.CreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails에서 User 객체를 추출하는 로직 필요
        Long userId = 1L; // 임시 값, 실제로는 UserDetails에서 추출

        Long applicationId = applicationService.createApplication(request, null); // 실제로는 User 객체 전달
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("지원서가 성공적으로 작성되었습니다.", applicationId));
    }

    /**
     * 지원서 생성 (비로그인 사용자)
     */
    @Operation(summary = "지원서 생성 (비로그인)",
            description = "비로그인 사용자가 이메일을 통해 지원서를 작성합니다.")
    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<Long>> createGuestApplication(
            @Valid @RequestBody ApplicationDto.CreateRequest request) {
        Long applicationId = applicationService.createApplication(request, null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("지원서가 성공적으로 작성되었습니다.", applicationId));
    }

    /**
     * 지원서 수정 (로그인 사용자)
     */
    @Operation(summary = "지원서 수정 (로그인)",
            description = "로그인한 사용자가 자신의 지원서를 수정합니다.")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDto.UpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails에서 User 객체를 추출하는 로직 필요
        applicationService.updateApplication(id, request, null); // 실제로는 User 객체 전달
        return ResponseEntity.ok(ApiResponse.success("지원서가 성공적으로 수정되었습니다.", null));
    }

    /**
     * 지원서 수정 (비로그인 사용자)
     */
    @Operation(summary = "지원서 수정 (비로그인)",
            description = "비로그인 사용자가 이메일을 통해 자신의 지원서를 수정합니다.")
    @PutMapping("/{id}/guest")
    public ResponseEntity<ApiResponse<Void>> updateGuestApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDto.UpdateRequest request) {
        applicationService.updateApplication(id, request, null);
        return ResponseEntity.ok(ApiResponse.success("지원서가 성공적으로 수정되었습니다.", null));
    }

    /**
     * 지원서 삭제 (로그인 사용자)
     */
    @Operation(summary = "지원서 삭제",
            description = "작성 중인(DRAFT 상태) 지원서를 삭제합니다.")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ApiResponse<Void>> deleteApplication(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        // UserDetails에서 User 객체를 추출하는 로직 필요
        applicationService.deleteApplication(id, null); // 실제로는 User 객체 전달
        return ResponseEntity.ok(ApiResponse.success("지원서가 성공적으로 삭제되었습니다.", null));
    }
}