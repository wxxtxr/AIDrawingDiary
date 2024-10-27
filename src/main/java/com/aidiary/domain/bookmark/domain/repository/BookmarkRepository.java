package com.aidiary.domain.bookmark.domain.repository;

import com.aidiary.domain.bookmark.domain.Bookmark;
import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, BookmarkQueryDslRepository {
    Optional<Bookmark> findByDiaryId(Long id);

    Optional<Bookmark> findByUserAndDiary(User user, Diary diary);

    Optional<Bookmark> findByUserIdAndDiaryId(Long id, Long diaryId);

    void deleteAllByDiary(Diary diary);
}
