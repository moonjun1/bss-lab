package com.bsslab.domain.post.dto;

import com.bsslab.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 응답 DTO")
public class PostResponse {
    @Schema(description = "게시글 ID", example = "1")
    private Long id;

    @Schema(description = "게시글 제목", example = "BSS-Lab 스프링 부트 스터디 모집 공고")
    private String title;

    @Schema(description = "게시글 내용", example = "안녕하세요. BSS-Lab에서 스프링 부트 스터디를 모집합니다.\n\n스터디는 매주 목요일 저녁 7시부터 9시까지 진행되며, 스프링 부트를 활용한 웹 애플리케이션 개발을 함께 공부합니다.")
    private String content;

    @Schema(description = "게시글 카테고리", example = "공지사항")
    private String category;

    @Schema(description = "조회수", example = "42")
    private Integer viewCount;

    @Schema(description = "게시글 상태", example = "PUBLISHED")
    private Post.Status status;

    @Schema(description = "작성자 이름", example = "admin")
    private String username;

    @Schema(description = "작성자 ID", example = "1")
    private Long userId;

    @Schema(description = "생성 일시", example = "2023-05-15T14:30:15")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2023-05-16T10:45:22")
    private LocalDateTime updatedAt;

    @Schema(description = "첨부된 이미지 목록")
    private List<PostImageResponse> images;

    public static PostResponse from(Post post) {
        List<PostImageResponse> imageResponses = post.getImages().stream()
                .map(PostImageResponse::from)
                .collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .viewCount(post.getViewCount())
                .status(post.getStatus())
                .username(post.getUser().getUsername())
                .userId(post.getUser().getId())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .images(imageResponses)
                .build();
    }
}