package com.aidiary.domain.emotion.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DiarysByEmotionRes(
        Long diaryId,
        String content,
        LocalDate diaryEntryDate
) {

    @QueryProjection
    public DiarysByEmotionRes(Long diaryId, String content, LocalDate diaryEntryDate) {
        this.diaryId = diaryId;
        this.content = content;
        this.diaryEntryDate = diaryEntryDate;
    }
}
