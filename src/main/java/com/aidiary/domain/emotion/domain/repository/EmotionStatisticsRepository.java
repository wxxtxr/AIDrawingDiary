package com.aidiary.domain.emotion.domain.repository;

import com.aidiary.domain.emotion.domain.EmotionStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionStatisticsRepository extends JpaRepository<EmotionStatistics, Long> {
    Optional<EmotionStatistics> findByUserId(Long userId);
}
