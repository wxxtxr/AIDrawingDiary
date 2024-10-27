package com.aidiary.domain.bookmark.application;

import com.aidiary.domain.bookmark.domain.Bookmark;
import com.aidiary.domain.bookmark.domain.repository.BookmarkRepository;
import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.diary.domain.repository.DiaryRepository;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.config.security.token.UserPrincipal;
import com.aidiary.global.payload.Message;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public Message diaryBookmark(UserPrincipal userPrincipal, Long diaryId) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(EntityNotFoundException::new);

        if (!diary.getUser().equals(user)) {
            return Message
                    .builder()
                    .message("해당하는 일기를 찾지 못했습니다.")
                    .build();
        }

        if (bookmarkRepository.findByDiaryId(diaryId).isPresent()) {
            return Message
                    .builder()
                    .message("이미 존재하는 북마크에 북마크 추가를 시도하였습니다.")
                    .build();
        }

        Bookmark bookmark = Bookmark.builder()
                .user(user)
                .diary(diary)
                .build();

        bookmarkRepository.save(bookmark);

        return Message
                .builder()
                .message("해당 일기에 북마크를 추가했습니다.")
                .build();
    }

    @Transactional
    public Message deleteDiaryBookmark(UserPrincipal userPrincipal, Long diaryId) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(EntityNotFoundException::new);

        if (bookmarkRepository.findByUserAndDiary(user, diary).isEmpty()) {
            return Message.builder()
                    .message("북마크 데이터를 찾을 수 없습니다.")
                    .build();
        }

        Bookmark bookmark = bookmarkRepository.findByUserIdAndDiaryId(userPrincipal.getId(), diaryId)
                .orElseThrow(EntityNotFoundException::new);

        bookmarkRepository.delete(bookmark);

        return Message.builder()
                .message("해당 일기의 북마크를 삭제했습니다.")
                .build();
    }
}
