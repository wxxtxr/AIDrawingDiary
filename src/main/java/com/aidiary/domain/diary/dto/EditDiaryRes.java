package com.aidiary.domain.diary.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record EditDiaryRes(
        Long userId,
        Long DiaryId,
        String content,
        LocalDate diaryEntryDate
) {
}
