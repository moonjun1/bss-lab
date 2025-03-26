package com.bsslab.domain.post.controller;

import com.bsslab.domain.post.dto.PostImageResponse;
import com.bsslab.domain.post.service.PostService;
import com.bsslab.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 이미지", description = "게시글에 이미지를 첨부하고 관리하기 위한 API")
public class PostImageController {

    private final PostService postService;

    @Operation(
            summary = "게시글 이미지 업로드",
            description = "게시글에 이미지를 업로드합니다. 한 번에 여러 개의 이미지를 업로드할 수 있습니다. 지원하는 이미지 형식: JPG, JPEG, PNG, GIF. 최대 파일 크기: 10MB.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "이미지 업로드 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PostImageResponse.class))
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (이미지 파일이 아니거나 크기 초과)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (본인 게시글이 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            )
    })
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<PostImageResponse>>> uploadImages(
            @Parameter(description = "이미지를 업로드할 게시글 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(
                    description = "업로드할 이미지 파일들 (다중 선택 가능)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("images") List<MultipartFile> images) {

        List<PostImageResponse> addedImages = postService.addImagesToPost(
                id,
                userDetails.getUsername(),
                images
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("이미지가 성공적으로 업로드되었습니다.", addedImages));
    }

    @Operation(
            summary = "게시글 이미지 삭제",
            description = "게시글에 첨부된 특정 이미지를 삭제합니다. 본인이 작성한 게시글의 이미지만 삭제할 수 있습니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "이미지 삭제 성공"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 (본인 게시글이 아님)"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글 또는 이미지를 찾을 수 없음"
            )
    })
    @DeleteMapping("/{postId}/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long postId,
            @Parameter(description = "삭제할 이미지 ID", example = "5")
            @PathVariable Long imageId,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePostImage(postId, imageId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("이미지가 성공적으로 삭제되었습니다.", null));
    }
}