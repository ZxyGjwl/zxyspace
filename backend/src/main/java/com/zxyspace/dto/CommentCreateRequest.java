package com.zxyspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateRequest {

    @NotBlank
    private String content;

    @NotNull
    private Long postId;
}