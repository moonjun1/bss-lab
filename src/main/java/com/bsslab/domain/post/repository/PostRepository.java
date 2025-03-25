package com.bsslab.domain.post.repository;

import com.bsslab.domain.post.entity.Post;
import com.bsslab.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatus(Post.Status status, Pageable pageable);
    Page<Post> findByCategoryAndStatus(String category, Post.Status status, Pageable pageable);
    Page<Post> findByUser(User user, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.status = :status")
    Page<Post> searchByKeyword(@Param("keyword") String keyword, @Param("status") Post.Status status, Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    Optional<Post> findByIdAndStatus(Long id, Post.Status status);
}