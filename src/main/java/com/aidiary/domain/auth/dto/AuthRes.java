package com.aidiary.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record AuthRes(
        @Schema(type = "string", example = "eyJhbGciOiJIUzUxMiJ9.eyJleHAiOjE2NTI3OTgxOTh9.6CoxHB_siOuz6PxsxHYQCgUT1_QbdyKTUwStQDutEd1-cIIARbQ0cyrnAmpIgi3IBoLRaqK7N1vXO42nYy4g5g", description = "access token 을 출력합니다.")
        String accessToken,

        @Schema( type = "string", example ="Bearer", description="권한(Authorization) 값 해더의 명칭을 지정합니다.")
        String tokenType,

        @Schema( type = "Role", example = "USER", description = "Role을 출력합니다.")
        String role,

        @Schema( type = "boolean", example = "false", description = "회원가입 완료 여부를 출력합니다.")
        boolean isRegistered
) {

    @Builder
    public AuthRes {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
        if (role == null) {
            role = "ROLE_USER";
        }
    }
}
