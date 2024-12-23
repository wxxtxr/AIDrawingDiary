package com.aidiary.domain.auth.application;

import com.aidiary.domain.auth.dto.NicknameRes;

import com.aidiary.domain.emotion.domain.EmotionStatistics;
import com.aidiary.domain.emotion.domain.repository.EmotionStatisticsRepository;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.config.security.token.UserPrincipal;
import com.aidiary.global.payload.Message;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final EmotionStatisticsRepository emotionStatisticsRepository;

    @Transactional
    public User findOrCreateUser(String provider, String idToken) {

        DecodedJWT decodedJWT = JWT.decode(idToken);
        String providerId = decodedJWT.getSubject();  // 사용자 고유 ID (sub)
        String email = "zidwkd00@naver.com";
        String username = decodedJWT.getClaim("nickname").asString();

        Optional<User> optionalUser = userRepository.findByProviderAndProviderId(provider, providerId);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            User newUser = new User("", username, email, "ROLE_USER", provider, providerId);
            return userRepository.save(newUser);
        }
    }


    @Transactional
    public String findEmail(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(EntityNotFoundException::new);
        System.out.println(user.getId()+" "+ user.getRole()+" "+user.getEmail());
        return user.getEmail();
    }


    @Transactional
    public NicknameRes updateNickname(UserPrincipal userPrincipal, String nickname) {
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(EntityNotFoundException::new);

        user.updateNickname(nickname);

        user.updateIsRegistered();

        // 통계 엔터티 생성
        EmotionStatistics emotionStatistics = EmotionStatistics.builder()
                .depressionCount(0)
                .happinessCount(0)
                .angerCount(0)
                .anxietyCount(0)
                .boringCount(0)
                .userId(userPrincipal.getId())
                .build();

        emotionStatisticsRepository.save(emotionStatistics);

        NicknameRes nicknameRes = NicknameRes.builder()
                .userid(user.getId())
                .nickname(nickname)
                .isRegistered(user.isRegistered())
                .build();


        return nicknameRes;
    }

    @Transactional
    public void updateFcmToken(Long id, String fcmToken) {
        User user = userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        user.updateFcmToken(fcmToken);
    }

    @Transactional
    public Message unlinkAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(EntityNotFoundException::new);
        userRepository.delete(user);
        return Message.builder()
                .message("회원 탈퇴에 성공 했습니다.")
                .build();
    }
}
