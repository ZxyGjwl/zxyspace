package com.zxyspace.controller;

import com.zxyspace.dto.PageResponse;
import com.zxyspace.dto.PostCreateRequest;
import com.zxyspace.dto.PostDto;
import com.zxyspace.dto.PostFilter;
import com.zxyspace.dto.PostSummaryDto;
import com.zxyspace.dto.PostUpdateRequest;
import com.zxyspace.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostSummaryDto>> getAllPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        PostFilter filter = new PostFilter();
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDir);
        
        return ResponseEntity.ok(postService.getAllPosts(filter));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<PageResponse<PostSummaryDto>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        PostFilter filter = new PostFilter();
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDir);
        
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId, filter));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<PostSummaryDto>> getPostsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        PostFilter filter = new PostFilter();
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDir);
        
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId, filter));
    }

    @GetMapping("/tag/{tagId}")
    public ResponseEntity<PageResponse<PostSummaryDto>> getPostsByTag(
            @PathVariable Long tagId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        PostFilter filter = new PostFilter();
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDir);
        
        return ResponseEntity.ok(postService.getPostsByTag(tagId, filter));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<PostSummaryDto>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {
        
        PostFilter filter = new PostFilter();
        filter.setKeyword(keyword);
        filter.setPage(page);
        filter.setSize(size);
        filter.setSortBy(sortBy);
        filter.setSortDirection(sortDir);
        
        return ResponseEntity.ok(postService.searchPosts(keyword, filter));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<PostSummaryDto>> getRecentPosts(
            @RequestParam(required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(postService.getRecentPosts(limit));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<PostSummaryDto>> getPopularPosts(
            @RequestParam(required = false, defaultValue = "5") int limit) {
        return ResponseEntity.ok(postService.getPopularPosts(limit));
    }

    @PostMapping("/author/{authorId}")
    public ResponseEntity<PostDto> createPost(
            @RequestBody PostCreateRequest postRequest,
            @PathVariable Long authorId) {
        return new ResponseEntity<>(postService.createPost(postRequest, authorId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(
            @PathVariable Long id, 
            @RequestBody PostUpdateRequest postRequest) {
        return ResponseEntity.ok(postService.updatePost(id, postRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<PostDto> incrementViews(@PathVariable Long id) {
        return ResponseEntity.ok(postService.incrementViews(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<PostDto> likePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.likePost(id));
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<PostDto> unlikePost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.unlikePost(id));
    }
} 