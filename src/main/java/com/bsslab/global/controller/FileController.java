package com.bsslab.global.controller;

import com.bsslab.global.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "파일", description = "업로드된 파일을 조회하는 API")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(
            summary = "파일 조회/다운로드",
            description = "업로드된 파일을 조회하거나 다운로드합니다. 이미지 파일의 경우 브라우저에서 직접 표시됩니다. " +
                    "파일 경로는 게시글 응답에 포함된 imageUrl 값을 사용합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "파일 조회 성공",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "파일을 찾을 수 없음"
            )
    })
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(
                    description = "파일 경로 (게시글 응답의 imageUrl 필드에서 얻을 수 있음)",
                    example = "posts/abc123-def456.jpg"
            )
            @PathVariable String fileName,
            HttpServletRequest request) {

        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}