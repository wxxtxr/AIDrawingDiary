package com.aidiary.domain.diary.presentation;

import com.aidiary.domain.diary.application.DiaryService;
import com.aidiary.domain.diary.dto.*;
import com.aidiary.domain.diary.dto.condition.DiariesSearchCondition;
import com.aidiary.global.config.security.token.CurrentUser;
import com.aidiary.global.config.security.token.UserPrincipal;
import com.aidiary.global.payload.ErrorResponse;
import com.aidiary.global.payload.Message;
import com.aidiary.global.payload.ResponseCustom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Diary", description = "Diary API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/diarys")
public class DiaryController {

    private final DiaryService diaryService;


    @Operation(summary = "일기 생성", description = "일기를 작성한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일기 생성 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreateDiaryRes.class))}),
            @ApiResponse(responseCode = "400", description = "일기 생성 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    public ResponseCustom<CreateDiaryRes> createDiary(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "Schemas의 CreateDiaryReq를 참고해주세요.", required = true) @Valid @RequestBody CreateDiaryReq createDiaryReq) {
        return ResponseCustom.OK(diaryService.writeDiary(userPrincipal, createDiaryReq));
    }

    @Operation(summary = "일기 상세 조회", description = "일기를 상세 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일기 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DiaryDetailsRes.class))}),
            @ApiResponse(responseCode = "400", description = "일기 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{diaryId}")
    public ResponseCustom<DiaryDetailsRes> viewDiaryDetail(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회할 일기의 id를 입력해주세요.", required = true) @PathVariable Long diaryId
    ) throws AccessDeniedException {
        return ResponseCustom.OK(diaryService.viewDiary(userPrincipal, diaryId));
    }

    @Operation(summary = "일기 수정", description = "작성된 일기 내용을 수정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일기 수정 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = EditDiaryRes.class))}),
            @ApiResponse(responseCode = "400", description = "일기 수정 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PatchMapping("/{id}")
    public ResponseCustom<EditDiaryRes> editDiary(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "수정할 content 내용을 입력해주세요.", required = true) @Valid @RequestBody EditDiaryReq editDiaryReq,
            @Parameter(description = "수정할 일기의 id를 입력해주세요.", required = true) @PathVariable Long id
    ) {
        return ResponseCustom.OK(diaryService.editDiary(userPrincipal, editDiaryReq, id));
    }

    @Operation(summary = "일기 삭제", description = "선택된 일기를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일기 삭제 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "일기 삭제 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/{id}")
    public ResponseCustom<Message> deleteDiary(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "삭제할 일기의 id를 입력해주세요.", required = true) @PathVariable Long id
    ) {
        return ResponseCustom.OK(diaryService.removeDiary(userPrincipal, id));
    }

    @Operation(summary = "일기 검색(내용) 조회", description = "일기를 검색 조건에 따라 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일기 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SearchDiariesRes.class))}),
            @ApiResponse(responseCode = "400", description = "일기 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/contentSearch")
    public ResponseCustom<Page<SearchDiariesRes>> findDiaries(
            @Parameter(description = "AccessToken 을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @ModelAttribute DiariesSearchCondition diariesSearchCondition,
            @Parameter(description = "조회 할 페이지와 페이지 크기를 입력해주세요.") Pageable pageable
    ) {
        return ResponseCustom.OK(diaryService.findDiariesByContent(userPrincipal, diariesSearchCondition, pageable));
    }

    @Operation(summary = "월 별 작성한 일기 조회", description = "월 별 작성한 일기들을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작성한 일기 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DiaryDetailsRes.class))}),
            @ApiResponse(responseCode = "400", description = "작성한 일기 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/monthlyDiaryList")
    public ResponseCustom<List<DiaryDetailsRes>> findMonthlyDiaryList(
            @Parameter(description = "AccessToken 을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "조회할 일기의 year를 입력해주세요.", required = true) @RequestParam int year,
            @Parameter(description = "조회할 일기의 month를 입력해주세요.", required = true) @RequestParam int month
            ) {
        return ResponseCustom.OK(diaryService.findMonthlyDiaries(userPrincipal, year, month));
    }
}
