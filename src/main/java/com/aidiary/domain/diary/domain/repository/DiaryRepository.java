package com.aidiary.domain.diary.domain.repository;

import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long>, DiaryQueryDslRepository {
    List<Diary> findAllByUserId(Long userId);

    Optional<Diary> findByUserAndDiaryEntryDate(User user, LocalDate date);
}
