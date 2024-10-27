package com.aidiary.domain.emotion.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

@Builder
public record EmotionStatRes(
        Integer angerCount,
        Integer depressionCount,
        Integer anxietyCount,
        Integer happinessCount,
        Integer boringCount
) {

    @QueryProjection
    public EmotionStatRes(Integer angerCount, Integer depressionCount, Integer anxietyCount, Integer happinessCount, Integer boringCount) {
        this.angerCount = angerCount;
        this.depressionCount = depressionCount;
        this.anxietyCount = anxietyCount;
        this.happinessCount = happinessCount;
        this.boringCount = boringCount;
    }
}
