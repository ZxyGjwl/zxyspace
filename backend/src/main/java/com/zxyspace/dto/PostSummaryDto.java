package com.zxyspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSummaryDto {

    private Long id;
    private String title;
    private String excerpt;
    private String coverImage;
    private UserSummaryDto author;
    private CategoryDto category;
    private Set<TagDto> tags;
    private int commentCount;
    private int views;
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean published;
} 