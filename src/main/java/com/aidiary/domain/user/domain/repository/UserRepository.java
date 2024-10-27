package com.aidiary.domain.user.domain.repository;

import com.aidiary.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    Optional<User> findByEmail(String email);

    Optional<User> findByProviderId(String providerId);

    @Query("SELECT u.id FROM User u")
    List<Long> findAllUserId();
}
