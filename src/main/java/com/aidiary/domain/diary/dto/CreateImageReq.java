package com.aidiary.domain.diary.dto;

import lombok.Builder;

@Builder
public record CreateImageReq (
        Long id,
        String content
)
{}