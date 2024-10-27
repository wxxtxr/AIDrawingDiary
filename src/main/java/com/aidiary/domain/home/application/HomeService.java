package com.aidiary.domain.home.application;

import com.aidiary.domain.diary.domain.repository.DiaryRepository;
import com.aidiary.domain.home.dto.HomePageWrapperRes;
import com.aidiary.domain.home.dto.HomeViewRes;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.config.security.token.UserPrincipal;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;


    @Transactional
    public HomePageWrapperRes loadHomePage(UserPrincipal userPrincipal) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        List<HomeViewRes> bookmarkedDiary = diaryRepository.findBookmarkedDiary(userPrincipal.getId());

        String nickname = user.getNickname();

        int consecutiveWritingDays = diaryRepository.findConsecutiveWritingDays(userPrincipal.getId());

        HomePageWrapperRes homePageWrapperRes = HomePageWrapperRes.builder()
                .nickname(nickname)
                .consecutiveWritingDays(consecutiveWritingDays)
                .recentFiveDiaries(bookmarkedDiary)
                .build();

        return homePageWrapperRes;
    }
}
