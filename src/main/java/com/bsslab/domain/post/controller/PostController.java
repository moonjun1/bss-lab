package com.bsslab.domain.post.controller;

import com.bsslab.domain.post.dto.PostListResponse;
import com.bsslab.domain.post.dto.PostRequest;
import com.bsslab.domain.post.dto.PostResponse;
import com.bsslab.domain.post.service.PostService;
import com.bsslab.global.dto.ApiResponse;
import com.bsslab.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시글 조회, 작성, 수정, 삭제를 위한 API")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "게시글 목록 조회",
            description = "모든 게시글을 페이지 단위로 조회합니다. 기본적으로 최신순(작성일 기준)으로 정렬되며, 페이지 크기는 10개입니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostListResponse>>> getPosts(
            @Parameter(description = "페이지네이션 정보 (페이지 번호, 크기, 정렬 기준)")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponse> postsPage = postService.getPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(postsPage)));
    }

    @Operation(
            summary = "카테고리별 게시글 조회",
            description = "특정 카테고리에 속한 게시글을 페이지 단위로 조회합니다. 기본적으로 최신순으로 정렬됩니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<PageResponse<PostListResponse>>> getPostsByCategory(
            @Parameter(description = "조회할 카테고리명", example = "공지사항")
            @PathVariable String category,
            @Parameter(description = "페이지네이션 정보 (페이지 번호, 크기, 정렬 기준)")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponse> postsPage = postService.getPostsByCategory(category, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("'%s' 카테고리의 게시글을 조회했습니다.", category),
                PageResponse.from(postsPage)));
    }

    @Operation(
            summary = "게시글 검색",
            description = "제목이나 내용에 특정 키워드가 포함된 게시글을 검색합니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "검색 성공",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PostListResponse>>> searchPosts(
            @Parameter(description = "검색 키워드", example = "스프링")
            @RequestParam String keyword,
            @Parameter(description = "페이지네이션 정보 (페이지 번호, 크기, 정렬 기준)")
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponse> postsPage = postService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("'%s' 키워드로 검색한 결과입니다.", keyword),
                PageResponse.from(postsPage)));
    }

    @Operation(
            summary = "게시글 상세 조회",
            description = "특정 게시글의 상세 내용을 조회합니다. 조회 시 조회수가 1 증가합니다. 이미지가 첨부된 경우 이미지 정보도 함께 반환됩니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = PostResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "게시글을 찾을 수 없음"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(
            @Parameter(description = "게시글 ID", example = "1")
            @PathVariable Long id) {
        PostResponse post = postService.getPost(id);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @Operation(
            summary = "게시글 작성",
            description = "새로운 게시글을 작성합니다. 이미지는 게시글 생성 후 별도로 업로드해야 합니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "게시글 생성 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패"
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 정보",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostRequest.class),
                            examples = @ExampleObject(
                                    value = "{\n  \"title\": \"BSS-Lab 스프링 부트 스터디 모집 공고\",\n  \"content\": \"안녕하세요. BSS-Lab에서 스프링 부트 스터디를 모집합니다.\",\n  \"category\": \"공지사항\",\n  \"status\": \"PUBLISHED\"\n}"
                            )
                    )
            )
            @Valid @RequestBody PostRequest requestDto) {
        Long postId = postService.createPost(userDetails.getUsername(), requestDto, Collections.emptyList());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 성공적으로 작성되었습니다.", postId));
    }

    @Operation(
            summary = "게시글 수정",
            description = "기존 게시글을 수정합니다. 본인이 작성한 게시글만 수정할 수 있습니다. 이미지는 별도 API를 통해 추가/삭제할 수 있습니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시글 수정 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 요청 데이터"
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
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @Parameter(description = "수정할 게시글 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostRequest requestDto) {
        Long postId = postService.updatePost(id, userDetails.getUsername(), requestDto, Collections.emptyList());
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 수정되었습니다.", postId));
    }

    @Operation(
            summary = "게시글 삭제",
            description = "게시글을 삭제합니다. 본인이 작성한 게시글만 삭제할 수 있습니다. 첨부된 이미지도 함께 삭제됩니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시글 삭제 성공"
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @Parameter(description = "삭제할 게시글 ID", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 삭제되었습니다.", null));
    }
}