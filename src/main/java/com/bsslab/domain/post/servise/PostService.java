package com.bsslab.domain.post.service;

import com.bsslab.domain.post.dto.PostImageResponse;
import com.bsslab.domain.post.dto.PostListResponse;
import com.bsslab.domain.post.dto.PostRequest;
import com.bsslab.domain.post.dto.PostResponse;
import com.bsslab.domain.post.entity.Post;
import com.bsslab.domain.post.entity.PostImage;
import com.bsslab.domain.post.repository.PostImageRepository;
import com.bsslab.domain.post.repository.PostRepository;
import com.bsslab.domain.user.entity.User;
import com.bsslab.domain.user.repository.UserRepository;
import com.bsslab.global.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.bsslab.global.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Long createPost(String username, PostRequest requestDto, List<MultipartFile> images) {
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

        post = postRepository.save(post);

        // Process images if provided
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    savePostImage(post, image);
                }
            }
        }

        return post.getId();
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findByStatus(Post.Status.PUBLISHED, pageable);
        return posts.map(PostListResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getPostsByCategory(String category, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategoryAndStatus(category, Post.Status.PUBLISHED, pageable);
        return posts.map(PostListResponse::from);
    }

    @Transactional
    public PostResponse getPost(Long id) {
        Post post = postRepository.findByIdAndStatus(id, Post.Status.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        postRepository.incrementViewCount(id);
        return PostResponse.from(post);
    }

    @Transactional
    public Long updatePost(Long id, String username, PostRequest requestDto, List<MultipartFile> newImages) {
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

        // Add new images if provided
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    savePostImage(post, image);
                }
            }
        }

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

        // Delete associated image files
        for (PostImage image : post.getImages()) {
            fileStorageService.deleteFile(image.getImageUrl());
        }

        postRepository.delete(post);
    }

    @Transactional
    public void deletePostImage(Long postId, Long imageId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Check if the user is the owner of the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this image");
        }

        PostImage postImage = post.getImages().stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        // Delete file from storage
        fileStorageService.deleteFile(postImage.getImageUrl());

        // Remove from post and delete from repository
        post.getImages().remove(postImage);
        postImageRepository.delete(postImage);
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> searchPosts(String keyword, Pageable pageable) {
        Page<Post> posts = postRepository.searchByKeyword(keyword, Post.Status.PUBLISHED, pageable);
        return posts.map(PostListResponse::from);
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
        // Get the post to delete its images
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        // Delete associated image files
        for (PostImage image : post.getImages()) {
            fileStorageService.deleteFile(image.getImageUrl());
        }

        postRepository.deleteById(id);
    }

    @Transactional
    public List<PostImageResponse> addImagesToPost(Long postId, String username, List<MultipartFile> images) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        // Check if the user is the owner of the post
        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to add images to this post");
        }

        List<PostImage> savedImages = images.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> savePostImage(post, file))
                .collect(Collectors.toList());

        return savedImages.stream()
                .map(PostImageResponse::from)
                .collect(Collectors.toList());
    }

    private PostImage savePostImage(Post post, MultipartFile file) {
        String storedFileName = fileStorageService.storeFile(file);

        PostImage postImage = PostImage.builder()
                .post(post)
                .imageUrl(storedFileName)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .build();

        post.addImage(postImage);
        return postImageRepository.save(postImage);
    }
}