package com.bsslab.domain.post.repository;

import com.bsslab.domain.post.entity.Post;
import com.bsslab.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);
    void deleteByPostId(Long postId);
}