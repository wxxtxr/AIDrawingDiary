package com.aidiary.domain.summary.domain;

import com.aidiary.domain.common.BaseEntity;
import com.aidiary.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiarySummary extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "summarized_diary", columnDefinition = "TEXT")
    private String summarizedDiary;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @Builder
    public DiarySummary(String summarizedDiary, User user) {
        this.summarizedDiary = summarizedDiary;
        this.user = user;
    }

    public void updateSummarizedDiary(String summarizedDiary) {
        this.summarizedDiary = summarizedDiary;
    }
}
