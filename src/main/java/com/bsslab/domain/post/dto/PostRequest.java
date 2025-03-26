package com.bsslab.domain.post.dto;

import com.bsslab.domain.post.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // 이 어노테이션은 getter, setter, equals, hashCode, toString을 자동 생성합니다
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 요청 DTO")
public class PostRequest {
    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(min = 2, max = 100, message = "제목은 2~100자 사이여야 합니다.")
    @Schema(description = "게시글 제목", example = "BSS-Lab 스프링 부트 스터디 모집 공고")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    @Schema(description = "게시글 내용", example = "안녕하세요. BSS-Lab에서 스프링 부트 스터디를 모집합니다.\n\n스터디는 매주 목요일 저녁 7시부터 9시까지 진행되며, 스프링 부트를 활용한 웹 애플리케이션 개발을 함께 공부합니다.")
    private String content;

    @NotBlank(message = "카테고리는 필수 입력값입니다.")
    @Schema(description = "게시글 카테고리", example = "공지사항", allowableValues = {"공지사항", "스터디", "프로젝트", "질문", "자유게시판"})
    private String category;

    @Schema(description = "게시글 상태", example = "PUBLISHED", allowableValues = {"PUBLISHED", "DRAFT", "DELETED"})
    private Post.Status status;
}