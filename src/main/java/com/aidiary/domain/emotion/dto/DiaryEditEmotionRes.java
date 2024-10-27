package com.aidiary.domain.emotion.dto;

import lombok.Builder;

@Builder
public record DiaryEditEmotionRes(
        Long diaryId,
        boolean isEdited,
        String emotion
) {
}
