package com.aidiary.domain.diary.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SearchDiariesRes(
        Long diaryId,
        String previewContent,
        LocalDate diaryEntryDate
) {

    @QueryProjection
    public SearchDiariesRes(Long diaryId, String previewContent, LocalDate diaryEntryDate) {
        this.diaryId = diaryId;
        this.previewContent = previewContent;
        this.diaryEntryDate = diaryEntryDate;
    }
}
