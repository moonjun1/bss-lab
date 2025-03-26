package com.bsslab.domain.application.controller;

import com.bsslab.domain.application.dto.ApplicationFormDto;
import com.bsslab.domain.application.service.ApplicationFormService;
import com.bsslab.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 지원 양식 관련 API 컨트롤러 (사용자용)
 */
@RestController
@RequestMapping("/forms")
@RequiredArgsConstructor
@Tag(name = "지원 양식", description = "지원 양식 조회 관련 API")
public class ApplicationFormController {

    private final ApplicationFormService applicationFormService;

    /**
     * 활성화된(지원 가능한) 지원 양식 목록 조회
     */
    @Operation(summary = "활성화된 지원 양식 목록 조회",
            description = "현재 지원 가능한(PUBLISHED 상태이며 기간 내인) 지원 양식 목록을 조회합니다.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ApplicationFormDto.ListResponse>>> getActiveApplicationForms() {
        List<ApplicationFormDto.ListResponse> activeForms = applicationFormService.getActiveApplicationForms();
        return ResponseEntity.ok(ApiResponse.success(activeForms));
    }

    /**
     * 지원 양식 상세 조회
     */
    @Operation(summary = "지원 양식 상세 조회",
            description = "지원 양식의 상세 정보와 포함된 질문 목록을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationFormDto.DetailResponse>> getApplicationForm(@PathVariable Long id) {
        ApplicationFormDto.DetailResponse form = applicationFormService.getApplicationForm(id);
        return ResponseEntity.ok(ApiResponse.success(form));
    }
}