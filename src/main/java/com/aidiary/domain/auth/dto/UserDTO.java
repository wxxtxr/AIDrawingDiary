package com.aidiary.domain.auth.dto;

import lombok.Builder;

@Builder
public record UserDTO(
        String role,
        String name,
        String username
) {
}
