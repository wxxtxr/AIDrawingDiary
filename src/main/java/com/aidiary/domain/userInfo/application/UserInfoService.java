package com.aidiary.domain.userInfo.application;

import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.domain.userInfo.dto.UserInfoRes;
import com.aidiary.global.config.security.token.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserInfoService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserInfoRes loadUserInfo(UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(RuntimeException::new);

        UserInfoRes userInfoRes = UserInfoRes.builder()
                .nickname(user.getNickname())
                .isRegistered(user.isRegistered())
                .build();

        return userInfoRes;
    }
}
