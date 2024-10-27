package com.aidiary.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record IdTokenReq(
        String idToken,
        String provider
) {
}