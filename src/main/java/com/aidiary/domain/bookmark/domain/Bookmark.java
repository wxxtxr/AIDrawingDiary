package com.aidiary.domain.bookmark.domain;

import com.aidiary.domain.common.BaseEntity;
import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;


    @Builder
    public Bookmark(User user, Diary diary) {
        this.user = user;
        this.diary = diary;
    }
}
