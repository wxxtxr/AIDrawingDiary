package com.aidiary.domain.summary.domain.repository;

import com.aidiary.domain.summary.domain.DiarySummary;
import com.aidiary.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiarySummaryRepository extends JpaRepository<DiarySummary, Long> {
    Optional<DiarySummary> findByUser(User user);
}
