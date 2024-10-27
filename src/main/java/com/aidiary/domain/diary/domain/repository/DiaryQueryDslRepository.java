package com.aidiary.domain.diary.domain.repository;

import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.diary.dto.DiaryDetailsRes;
import com.aidiary.domain.diary.dto.SearchDiariesRes;
import com.aidiary.domain.diary.dto.condition.DiariesSearchCondition;
import com.aidiary.domain.emotion.dto.DiarysByEmotionRes;
import com.aidiary.domain.emotion.dto.EmotionStatRes;
import com.aidiary.domain.home.dto.HomeViewRes;
import com.aidiary.domain.user.domain.User;
import com.aidiary.global.config.security.token.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;

public interface DiaryQueryDslRepository {

    List<HomeViewRes> findRecentFiveDiaryWithAuthorization(Long userId);

    int findConsecutiveWritingDays(Long userId);

    EmotionStatRes findEmotionsCountBetweenStartDateAndEndDate(Long id, LocalDate startDate, LocalDate endDate);

    Page<SearchDiariesRes> findDiaries(User user, DiariesSearchCondition diariesSearchCondition, Pageable pageable);

    Slice<DiarysByEmotionRes> findAllByEmotionAndUserId(String emotion, Long id, Pageable pageable);

    List<DiaryDetailsRes> findByUserIdWithYearAndMonth(Long id, int year, int month);

    DiaryDetailsRes findOneByUserIdAndDiaryId(Long id, Long diaryId);

    List<HomeViewRes> findBookmarkedDiary(Long userId);
}
