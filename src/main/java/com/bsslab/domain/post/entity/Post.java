package com.bsslab.domain.post.entity;

import com.bsslab.common.entity.BaseTimeEntity;
import com.bsslab.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Builder
    public Post(User user, String title, String content, Integer viewCount, String category, Status status) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount != null ? viewCount : 0;
        this.category = category;
        this.status = status;
    }

    public void incrementViewCount() {
        this.viewCount += 1;
    }

    public void addImage(PostImage image) {
        this.images.add(image);
        image.setPost(this);
    }

    public enum Status {
        PUBLISHED, DRAFT, DELETED
    }
}