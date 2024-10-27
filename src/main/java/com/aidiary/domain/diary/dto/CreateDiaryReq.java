package com.aidiary.domain.diary.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record CreateDiaryReq(
        String content,
        LocalDate diaryEntryDate
) {
}
