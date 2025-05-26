package com.zxyspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String content;
    private UserSummaryDto user;
    private Long postId;
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}