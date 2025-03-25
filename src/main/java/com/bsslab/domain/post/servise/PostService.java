package com.bsslab.domain.post.service;

import com.bsslab.domain.post.dto.PostDto;
import com.bsslab.domain.post.entity.Post;
import com.bsslab.domain.post.repository.PostRepository;
import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.repository.UserRepository;
import com.bsslab.global.exception.GlobalExceptionHandler.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createPost(String username, PostDto.Request requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Post.Status status = requestDto.getStatus() != null ? requestDto.getStatus() : Post.Status.PUBLISHED;

        Post post = Post.builder()
                .user(user)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .category(requestDto.getCategory())
                .viewCount(0)
                .status(status)
                .build();

        return postRepository.save(post).getId();
    }

    @Transactional(readOnly = true)
    public Page<PostDto.ListResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByStatus(Post.Status.PUBLISHED, pageable);
        return posts.map(PostDto.ListResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.ListResponse> getPostsByCategory(String category, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategoryAndStatus(category, Post.Status.PUBLISHED, pageable);
        return posts.map(PostDto.ListResponse::from);
    }

    @Transactional
    public PostDto.Response getPost(Long id) {
        Post post = postRepository.findByIdAndStatus(id, Post.Status.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        postRepository.incrementViewCount(id);
        return PostDto.Response.from(post);
    }

    @Transactional
    public Long updatePost(Long id, String username, PostDto.Request requestDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Check if the user is the owner of the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to update this post");
        }

        post.setTitle(requestDto.getTitle());
        post.setContent(requestDto.getContent());
        post.setCategory(requestDto.getCategory());
        post.setStatus(requestDto.getStatus());

        return postRepository.save(post).getId();
    }

    @Transactional
    public void deletePost(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Check if the user is the owner of the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    @Transactional(readOnly = true)
    public Page<PostDto.ListResponse> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.searchByKeyword(keyword, Post.Status.PUBLISHED, pageable);
        return posts.map(PostDto.ListResponse::from);
    }

    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Transactional
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }
}