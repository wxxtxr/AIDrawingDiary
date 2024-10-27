package com.aidiary.domain.user.domain;

import com.aidiary.domain.bookmark.domain.Bookmark;
import com.aidiary.domain.chatbot.domain.ChatHistory;
import com.aidiary.domain.common.BaseEntity;
import com.aidiary.domain.diary.domain.Diary;
import com.aidiary.domain.summary.domain.DiarySummary;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    private String nickname;

    private String username;

    private String email;

    private String role;

    private String provider;

    private String providerId;

    private boolean isRegistered;

    private String fcmToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, optional = false)
    private DiarySummary diarySummary;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Bookmark> bookmark = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatHistory> chatHistories = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Diary> diary = new ArrayList<>();


    @Builder
    public User(String nickname, String username, String email, String role, String provider, String providerId) {
        this.nickname = nickname;
        this.username = username;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.isRegistered = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateIsRegistered() {
        this.isRegistered = true;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateDiarySummary(DiarySummary diarySummary) {
        this.diarySummary = diarySummary;
    }
}
