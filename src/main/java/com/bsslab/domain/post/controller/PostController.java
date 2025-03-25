package com.bsslab.domain.post.controller;

import com.bsslab.domain.post.dto.PostDto;
import com.bsslab.domain.post.service.PostService;
import com.bsslab.global.dto.ApiResponse;
import com.bsslab.global.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
@Tag(name = "게시판", description = "게시판 관련 API")
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 목록 조회", description = "모든 게시글을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostDto.ListResponse>>> getPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto.ListResponse> postsPage = postService.getPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(postsPage)));
    }

    @Operation(summary = "카테고리별 게시글 조회", description = "특정 카테고리의 게시글을 페이지 단위로 조회합니다.")
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<PageResponse<PostDto.ListResponse>>> getPostsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto.ListResponse> postsPage = postService.getPostsByCategory(category, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("'%s' 카테고리의 게시글을 조회했습니다.", category),
                PageResponse.from(postsPage)));
    }

    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<PostDto.ListResponse>>> searchPosts(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostDto.ListResponse> postsPage = postService.searchPosts(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(
                String.format("'%s' 키워드로 검색한 결과입니다.", keyword),
                PageResponse.from(postsPage)));
    }

    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 내용을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostDto.Response>> getPost(@PathVariable Long id) {
        PostDto.Response post = postService.getPost(id);
        return ResponseEntity.ok(ApiResponse.success(post));
    }

    @Operation(summary = "게시글 작성", description = "새로운 게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostDto.Request requestDto) {
        Long postId = postService.createPost(userDetails.getUsername(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("게시글이 성공적으로 작성되었습니다.", postId));
    }

    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updatePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PostDto.Request requestDto) {
        Long postId = postService.updatePost(id, userDetails.getUsername(), requestDto);
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 수정되었습니다.", postId));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 삭제되었습니다.", null));
    }
}