package com.zxyspace.service;

import com.zxyspace.dto.PageResponse;
import com.zxyspace.dto.PostCreateRequest;
import com.zxyspace.dto.PostDto;
import com.zxyspace.dto.PostFilter;
import com.zxyspace.dto.PostSummaryDto;
import com.zxyspace.dto.PostUpdateRequest;

import java.util.List;

public interface PostService {
    
    PostDto getPostById(Long id);
    
    PageResponse<PostSummaryDto> getAllPosts(PostFilter filter);
    
    PageResponse<PostSummaryDto> getPostsByAuthor(Long authorId, PostFilter filter);
    
    PageResponse<PostSummaryDto> getPostsByCategory(Long categoryId, PostFilter filter);
    
    PageResponse<PostSummaryDto> getPostsByTag(Long tagId, PostFilter filter);
    
    PageResponse<PostSummaryDto> searchPosts(String keyword, PostFilter filter);
    
    List<PostSummaryDto> getRecentPosts(int limit);
    
    List<PostSummaryDto> getPopularPosts(int limit);
    
    PostDto createPost(PostCreateRequest postRequest, Long authorId);
    
    PostDto updatePost(Long id, PostUpdateRequest postRequest);
    
    void deletePost(Long id);
    
    PostDto incrementViews(Long id);
    
    PostDto likePost(Long id);
    
    PostDto unlikePost(Long id);
} 