package com.aidiary.domain.diary.dto;

import lombok.Builder;

@Builder
public record CreateImageReq (
        String id,
        String content
)
{}