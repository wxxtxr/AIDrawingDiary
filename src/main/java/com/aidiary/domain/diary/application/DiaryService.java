package com.aidiary.domain.diary.application;

import com.aidiary.domain.bookmark.domain.repository.BookmarkRepository;
import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.diary.domain.repository.DiaryRepository;
import com.aidiary.domain.diary.dto.*;
import com.aidiary.domain.diary.dto.condition.DiariesSearchCondition;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.config.security.token.UserPrincipal;
import com.aidiary.global.payload.Message;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public CreateDiaryRes writeDiary(UserPrincipal userPrincipal, CreateDiaryReq createDiaryReq) {

        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(EntityNotFoundException::new);

        Diary diary = Diary.builder()
                .user(user)
                .content(createDiaryReq.content())
                .diaryEntryDate(createDiaryReq.diaryEntryDate())
                .build();

        diaryRepository.save(diary);

        CreateDiaryRes createDiaryRes = CreateDiaryRes.builder()
                .id(diary.getId())
                .content(diary.getContent())
                .diaryEntryDate(diary.getDiaryEntryDate())
                .build();

        return createDiaryRes;
    }

    @Transactional
    public EditDiaryRes editDiary(UserPrincipal userPrincipal, EditDiaryReq editDiaryReq, Long id) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        Diary diary = diaryRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        if (user.getId().equals(diary.getUser().getId())) {
            diary.updateContent(editDiaryReq.content());

            EditDiaryRes editDiaryRes = EditDiaryRes.builder()
                    .userId(user.getId())
                    .DiaryId(diary.getId())
                    .content(editDiaryReq.content())
                    .diaryEntryDate(diary.getDiaryEntryDate())
                    .build();

            return editDiaryRes;
        }

        return null;
    }

    @Transactional
    public Message removeDiary(UserPrincipal userPrincipal, Long id) {
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        if (!user.equals(diary.getUser())) {

            return Message.builder()
                    .message("일기 삭제에 실패했습니다.")
                    .build();

        }

            bookmarkRepository.deleteAllByDiary(diary);
            diaryRepository.delete(diary);

            return Message.builder()
                    .message("일기를 삭제하였습니다.")
                    .build();
    }

    @Transactional
    public DiaryDetailsRes viewDiary(UserPrincipal userPrincipal, Long diaryId) throws AccessDeniedException {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(EntityNotFoundException::new);

        if (!diary.getUser().equals(user)) {
            throw new AccessDeniedException("해당 일기에 접근 권한이 없습니다.");
        }


        DiaryDetailsRes oneByUserIdAndDiaryId = diaryRepository.findOneByUserIdAndDiaryId(userPrincipal.getId(), diaryId);

        return oneByUserIdAndDiaryId;

    }

    @Transactional
    public Page<SearchDiariesRes> findDiariesByContent(UserPrincipal userPrincipal, DiariesSearchCondition diariesSearchCondition, Pageable pageable) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        return diaryRepository.findDiaries(user, diariesSearchCondition, pageable);
    }

    @Transactional
    public List<DiaryDetailsRes> findMonthlyDiaries(UserPrincipal userPrincipal, int year, int month) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);
        return diaryRepository.findByUserIdWithYearAndMonth(user.getId(), year, month);
    }
}
