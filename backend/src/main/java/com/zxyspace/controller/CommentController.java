package com.zxyspace.controller;

import com.zxyspace.dto.CommentCreateRequest;
import com.zxyspace.dto.CommentDto;
import com.zxyspace.dto.PageResponse;
import com.zxyspace.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/post/{postId}/page")
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByPostIdPaginated(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId, page, size));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PageResponse<CommentDto>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId, page, size));
    }

    @PostMapping("/post/{postId}/user/{userId}")
    public ResponseEntity<CommentDto> createComment(
            @RequestBody CommentCreateRequest commentRequest,
            @PathVariable Long postId,
            @PathVariable Long userId) {
        return new ResponseEntity<>(commentService.createComment(commentRequest, postId, userId), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long id,
            @RequestBody String content) {
        return ResponseEntity.ok(commentService.updateComment(id, content));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<CommentDto> likeComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.likeComment(id));
    }

    @PostMapping("/{id}/unlike")
    public ResponseEntity<CommentDto> unlikeComment(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.unlikeComment(id));
    }

    @GetMapping("/count/post/{postId}")
    public ResponseEntity<Integer> countCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.countCommentsByPostId(postId));
    }
} 