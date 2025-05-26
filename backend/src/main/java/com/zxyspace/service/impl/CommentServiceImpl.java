package com.zxyspace.service.impl;

import com.zxyspace.dto.CommentCreateRequest;
import com.zxyspace.dto.CommentDto;
import com.zxyspace.dto.PageResponse;
import com.zxyspace.dto.UserSummaryDto;
import com.zxyspace.exception.ResourceNotFoundException;
import com.zxyspace.model.Comment;
import com.zxyspace.model.Post;
import com.zxyspace.model.User;
import com.zxyspace.repository.CommentRepository;
import com.zxyspace.repository.PostRepository;
import com.zxyspace.repository.UserRepository;
import com.zxyspace.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, 
                             PostRepository postRepository, 
                             UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        return mapToCommentDto(comment);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        return commentRepository.findByPost(post).stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<CommentDto> getCommentsByPostId(Long postId, int page, int size) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findByPost(post, pageable);
        
        List<CommentDto> content = commentsPage.getContent().stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                commentsPage.getNumber(),
                commentsPage.getSize(),
                commentsPage.getTotalElements(),
                commentsPage.getTotalPages(),
                commentsPage.isLast()
        );
    }

    @Override
    public PageResponse<CommentDto> getCommentsByUserId(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentsPage = commentRepository.findByUser(user, pageable);
        
        List<CommentDto> content = commentsPage.getContent().stream()
                .map(this::mapToCommentDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                commentsPage.getNumber(),
                commentsPage.getSize(),
                commentsPage.getTotalElements(),
                commentsPage.getTotalPages(),
                commentsPage.isLast()
        );
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentCreateRequest commentRequest, Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Comment comment = Comment.builder()
                .content(commentRequest.getContent())
                .post(post)
                .user(user)
                .build();
        
        post.addComment(comment);
        Comment savedComment = commentRepository.save(comment);
        
        return mapToCommentDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        
        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);
        
        return mapToCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        
        // 从Post中移除评论
        Post post = comment.getPost();
        post.removeComment(comment);
        
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentDto likeComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        
        comment.like();
        Comment updatedComment = commentRepository.save(comment);
        
        return mapToCommentDto(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto unlikeComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        
        comment.unlike();
        Comment updatedComment = commentRepository.save(comment);
        
        return mapToCommentDto(updatedComment);
    }

    @Override
    public int countCommentsByPostId(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        
        return commentRepository.countByPost(post);
    }
    
    // 辅助方法：将Comment实体映射为CommentDto
    private CommentDto mapToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .user(mapToUserSummaryDto(comment.getUser()))
                .likes(comment.getLikes())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
    
    // 辅助方法：将User实体映射为UserSummaryDto
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
} 