package com.aidiary.domain.home.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record HomePageWrapperRes(
        String nickname,
        int consecutiveWritingDays,
        List<HomeViewRes> recentFiveDiaries
) {
}
