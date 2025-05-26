package com.zxyspace.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class PostUpdateRequest {

    @Size(max = 100)
    private String title;

    @Size(max = 255)
    private String excerpt;

    private String content;

    private String coverImage;

    private Long categoryId;

    private Set<Long> tagIds;

    private Boolean published;
} 