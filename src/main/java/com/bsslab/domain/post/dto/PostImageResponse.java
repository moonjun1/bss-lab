package com.bsslab.domain.post.dto;

import com.bsslab.domain.post.entity.PostImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 이미지 응답 DTO")
public class PostImageResponse {
    @Schema(description = "이미지 ID", example = "1")
    private Long id;

    @Schema(description = "이미지 URL", example = "posts/abc123-def456.jpg")
    private String imageUrl;

    @Schema(description = "파일 이름", example = "my-image.jpg")
    private String fileName;

    @Schema(description = "파일 타입", example = "image/jpeg")
    private String fileType;

    @Schema(description = "파일 크기(바이트)", example = "153284")
    private Long fileSize;

    public static PostImageResponse from(PostImage postImage) {
        return PostImageResponse.builder()
                .id(postImage.getId())
                .imageUrl(postImage.getImageUrl())
                .fileName(postImage.getFileName())
                .fileType(postImage.getFileType())
                .fileSize(postImage.getFileSize())
                .build();
    }
}