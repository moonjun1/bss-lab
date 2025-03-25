package com.bsslab.domain.post.dto;

import com.bsslab.domain.post.entity.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        @Size(min = 2, max = 100, message = "제목은 2~100자 사이여야 합니다.")
        private String title;

        @NotBlank(message = "내용은 필수 입력값입니다.")
        private String content;

        @NotBlank(message = "카테고리는 필수 입력값입니다.")
        private String category;

        private Post.Status status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String category;
        private Integer viewCount;
        private Post.Status status;
        private String username;
        private Long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static Response from(Post post) {
            return Response.builder()
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
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ListResponse {
        private Long id;
        private String title;
        private String category;
        private Integer viewCount;
        private Post.Status status;
        private String username;
        private LocalDateTime createdAt;

        public static ListResponse from(Post post) {
            return ListResponse.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .category(post.getCategory())
                    .viewCount(post.getViewCount())
                    .status(post.getStatus())
                    .username(post.getUser().getUsername())
                    .createdAt(post.getCreatedAt())
                    .build();
        }
    }
}