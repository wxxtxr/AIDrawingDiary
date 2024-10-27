package com.aidiary.domain.diary.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateDiaryRes(
        Long id,
        String content,
        LocalDate diaryEntryDate
) {
}
