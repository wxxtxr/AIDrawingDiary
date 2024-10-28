package com.aidiary.domain.home.dto;

import com.aidiary.domain.diary.dto.DiaryDetailsRes;
import lombok.Builder;

import java.util.List;

@Builder
public record HomePageWrapperRes(
        String nickname,
        int consecutiveWritingDays,
        List<HomeViewRes> recentFiveDiaries,
        List<DiaryDetailsRes> recentMonthDiaries
) {
}
