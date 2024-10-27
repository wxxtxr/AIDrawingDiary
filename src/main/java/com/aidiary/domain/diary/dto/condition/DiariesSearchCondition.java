package com.aidiary.domain.diary.dto.condition;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record DiariesSearchCondition(

        @Schema(type = "string", example = "용산 엑스포에 놀러갔던 날", description = "content로 일기 내용을 검색합니다.")
        String content
) {
}
