package com.aidiary.domain.emotion.domain;

import com.aidiary.domain.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmotionStatistics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Integer angerCount;
    private Integer depressionCount;
    private Integer anxietyCount;
    private Integer happinessCount;
    private Integer boringCount;

    private Long userId;


    @Builder
    public EmotionStatistics(Integer angerCount, Integer depressionCount, Integer anxietyCount, Integer happinessCount, Integer boringCount, Long userId) {
        this.angerCount = angerCount;
        this.depressionCount = depressionCount;
        this.anxietyCount = anxietyCount;
        this.happinessCount = happinessCount;
        this.boringCount = boringCount;
        this.userId = userId;
    }


    public void updateEmotionStats(String emotion) {
        if (emotion.equals("화남")) {
            this.angerCount += 1;
        } else if (emotion.equals("우울")) {
            this.depressionCount += 1;
        } else if (emotion.equals("불안")) {
            this.anxietyCount += 1;
        } else if (emotion.equals("행복")) {
            this.happinessCount += 1;
        } else if (emotion.equals("평온")) {
            this.boringCount += 1;
        }
    }
}
