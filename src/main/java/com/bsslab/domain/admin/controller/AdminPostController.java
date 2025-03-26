package com.bsslab.domain.admin.controller;

import com.bsslab.domain.post.dto.PostListResponse;
import com.bsslab.domain.post.entity.Post;
import com.bsslab.domain.post.service.PostService;
import com.bsslab.global.dto.ApiResponse;
import com.bsslab.global.dto.PageResponse;
import com.bsslab.global.exception.GlobalExceptionHandler.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/posts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "관리자 게시판", description = "관리자용 게시판 관리 API")
public class AdminPostController {

    private final PostService postService;

    @Operation(summary = "모든 게시글 조회(관리자용)", description = "관리자가 모든 게시글을 페이지 단위로 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<PostListResponse>>> getAllPosts(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponse> postsPage = postService.getPosts(pageable);
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(postsPage)));
    }

    @Operation(summary = "게시글 상태 변경(관리자용)", description = "관리자가 게시글의 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updatePostStatus(
            @PathVariable Long id,
            @RequestParam Post.Status status) {

        // 관리자는 게시글의 상태만 변경 가능
        Post post = postService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        post.setStatus(status);
        postService.save(post);

        return ResponseEntity.ok(ApiResponse.success(
                String.format("게시글 상태가 %s로 변경되었습니다.", status), null));
    }

    @Operation(summary = "게시글 삭제(관리자용)", description = "관리자가 게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        // 관리자는 어떤 게시글이든 삭제 가능
        Post post = postService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        postService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("게시글이 성공적으로 삭제되었습니다.", null));
    }
}