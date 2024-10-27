package com.aidiary.domain.diary.domain;

import com.aidiary.domain.bookmark.domain.Bookmark;
import com.aidiary.domain.common.BaseEntity;
import com.aidiary.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "diary_entry_date")
    private LocalDate diaryEntryDate;

    @Column(name = "emotion")
    private String emotion;

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmark = new ArrayList<>();


    @Builder
    public Diary(User user, String content, LocalDate diaryEntryDate) {
        this.user = user;
        this.content = content;
        this.diaryEntryDate = diaryEntryDate;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateDiaryEntryDate(LocalDate diaryEntryDate) {
        this.diaryEntryDate = diaryEntryDate;
    }

    public void updateEmotion(String emotion) {
        this.emotion = emotion;
    }

}
