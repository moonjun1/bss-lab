package com.bsslab.domain.post.dto;

import com.bsslab.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 목록 응답 DTO")
public class PostListResponse {
    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "BSS-Lab 스프링 부트 스터디 모집 공고")
    private String title;

    @Schema(description = "게시글 카테고리", example = "공지사항")
    private String category;

    @Schema(description = "조회수", example = "42")
    private Integer viewCount;

    @Schema(description = "게시글 상태", example = "PUBLISHED")
    private Post.Status status;

    @Schema(description = "작성자 이름", example = "admin")
    private String username;

    @Schema(description = "생성 일시", example = "2023-05-15T14:30:15")
    private LocalDateTime createdAt;

    @Schema(description = "첨부 이미지 여부", example = "true")
    private Boolean hasImage;

    @Schema(description = "첫 번째 이미지 URL (썸네일)", example = "posts/abc123-def456.jpg")
    private String thumbnailUrl;

    public static PostListResponse from(Post post) {
        boolean hasImage = !post.getImages().isEmpty();
        String thumbnailUrl = null;

        if (hasImage) {
            thumbnailUrl = Optional.ofNullable(post.getImages().get(0))
                    .map(image -> image.getImageUrl())
                    .orElse(null);
        }

        return PostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .viewCount(post.getViewCount())
                .status(post.getStatus())
                .username(post.getUser().getUsername())
                .createdAt(post.getCreatedAt())
                .hasImage(hasImage)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
}