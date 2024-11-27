package com.aidiary.domain.diary.presentation;

import com.aidiary.domain.diary.application.DiaryService;
import com.aidiary.domain.diary.dto.*;
import com.aidiary.domain.diary.dto.condition.DiariesSearchCondition;
import com.aidiary.domain.summary.application.SummaryService;
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
import java.util.UUID;

@Tag(name = "Diary", description = "Diary API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/diarys")
public class DiaryController {

    private final DiaryService diaryService;


    private final SummaryService summaryService;
    @PostMapping("/test")
    public void test() throws Exception {
        String diaryEntry = "오늘은 정말 다사다난한 하루였다. 아침부터 일어나자마자 해야 할 일들이 떠오르면서 살짝 피곤함이 몰려왔지만, 그래도 하루를 열심히 시작하기로 마음먹었다. 간단하게 아침 식사를 마친 후, 평소처럼 컴퓨터 앞에 앉아 오늘 처리해야 할 업무를 정리했다. 오전에는 주로 프로젝트의 코드를 수정하고, 발생하는 에러들을 해결하는 데 시간을 보냈다. 특히 예상치 못한 예외 처리 문제로 인해 애를 먹었다. 예외가 발생할 때 팝업 다이얼로그로 에러 메시지를 표시하려고 했는데, 생각보다 간단하지 않았다. 코드를 여러 번 고치고 수정하면서 점점 그 과정에서 실력을 쌓아가고 있다는 느낌이 들었다. 점심시간이 되어서 간단하게 식사를 하고, 잠시 산책을 하며 머리를 식혔다. 바깥 공기를 마시니 머리가 맑아지는 기분이었다. 요즘은 가을이라 바람이 서늘하게 불어서 산책하기 정말 좋은 날씨다. 잠시 자연을 만끽하며 하루의 스트레스를 잊고, 새로운 아이디어도 떠올렸다. 오후에는 다시 컴퓨터 앞에 앉아 코드를 이어서 수정했다. 그리고 프로젝트의 마지막 배포 단계를 준비했다. 배포 과정 중에 예상하지 못한 포트 충돌 문제가 생겨서 당황했지만, 차근차근 문제를 해결해 나갔다. 포트를 여러 번 수정하고 죽이는 과정을 반복하면서 결국 문제를 해결했다. 마침내 애플리케이션이 제대로 실행되었을 때의 성취감은 말로 표현하기 어려웠다. 저녁이 되자 하루가 꽤 길게 느껴졌다. 하지만 오늘의 목표를 대부분 달성한 덕분에 뿌듯한 마음으로 하루를 마무리할 수 있었다. 간단히 저녁 식사를 하고 나서, 남은 시간에는 내가 좋아하는 음악을 들으며 느긋하게 휴식을 취했다. 오늘 하루도 이렇게 알차게 보냈다는 생각에 뿌듯했다. 이 일기를 안에 글 없이 그려줘 나는 27세 한국인 여성이야";
        summaryService.createImage(diaryEntry);
    }

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
