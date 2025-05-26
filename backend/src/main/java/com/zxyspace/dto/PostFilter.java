package com.zxyspace.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostFilter {

    private String keyword;
    private Long categoryId;
    private List<Long> tagIds;
    private Long authorId;
    private Boolean published;
    private String sortBy = "createdAt";
    private String sortDirection = "desc";
    private int page = 0;
    private int size = 10;
} 