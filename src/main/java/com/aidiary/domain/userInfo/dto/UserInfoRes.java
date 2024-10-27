package com.aidiary.domain.userInfo.dto;

import lombok.Builder;

@Builder
public record UserInfoRes(
        String nickname,
        Boolean isRegistered
) {
}
