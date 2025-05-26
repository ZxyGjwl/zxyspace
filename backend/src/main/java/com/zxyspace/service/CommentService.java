package com.zxyspace.service;

import com.zxyspace.dto.CommentCreateRequest;
import com.zxyspace.dto.CommentDto;
import com.zxyspace.dto.PageResponse;

import java.util.List;

public interface CommentService {
    
    CommentDto getCommentById(Long id);
    
    List<CommentDto> getCommentsByPostId(Long postId);
    
    PageResponse<CommentDto> getCommentsByPostId(Long postId, int page, int size);
    
    PageResponse<CommentDto> getCommentsByUserId(Long userId, int page, int size);
    
    CommentDto createComment(CommentCreateRequest commentRequest, Long postId, Long userId);
    
    CommentDto updateComment(Long id, String content);
    
    void deleteComment(Long id);
    
    CommentDto likeComment(Long id);
    
    CommentDto unlikeComment(Long id);
    
    int countCommentsByPostId(Long postId);
} 