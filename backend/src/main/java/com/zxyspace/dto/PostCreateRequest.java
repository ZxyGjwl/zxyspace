package com.zxyspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class PostCreateRequest {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 255)
    private String excerpt;

    @NotBlank
    private String content;

    private String coverImage;

    private Long categoryId;

    private Set<Long> tagIds;

    private boolean published = true;
}