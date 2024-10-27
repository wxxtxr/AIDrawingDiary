package com.aidiary.domain.auth.presentation;

import com.aidiary.domain.auth.dto.AuthRes;
import com.aidiary.domain.auth.dto.IdTokenReq;
import com.aidiary.domain.auth.application.AuthService;
import com.aidiary.domain.auth.dto.NicknameRes;
import com.aidiary.domain.user.domain.User;
import com.aidiary.domain.user.domain.repository.UserRepository;
import com.aidiary.global.config.security.jwt.JWTUtil;
import com.aidiary.global.config.security.oidc.OidcProviderFactory;
import com.aidiary.global.config.security.oidc.Provider;
import com.aidiary.global.config.security.token.CurrentUser;
import com.aidiary.global.config.security.token.UserPrincipal;
import com.aidiary.global.payload.ErrorResponse;
import com.aidiary.global.payload.Message;
import com.aidiary.global.payload.ResponseCustom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authorization", description = "Authorization API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OidcProviderFactory oidcProviderFactory;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;


    private final AuthService authService;


    @Operation(summary = "소셜 로그인", description = "idToken과 provider(ex. kakao)로 소셜 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AuthRes.class)))}),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/login")
    public ResponseCustom<AuthRes> login(@RequestBody IdTokenReq idTokenReq) {
        System.out.println("idTokenReq = " + idTokenReq.idToken());
        String providerId = oidcProviderFactory.getProviderId(
                Provider.valueOf(idTokenReq.provider().toUpperCase()), idTokenReq.idToken());

        if (providerId == null) {
            return ResponseCustom.INVALID_ID_TOKEN();
        }

        authService.findOrCreateUser(idTokenReq.provider(), idTokenReq.idToken());
        String email = authService.findEmail(providerId);

        String accessToken = jwtUtil.createJwt("access", providerId, "ROLE_USER", 3600000L, email);

        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(EntityNotFoundException::new);


        AuthRes authRes = AuthRes.builder()
                .accessToken(accessToken)
                .isRegistered(user.isRegistered())
                .build();

        return ResponseCustom.OK(authRes);
    }

    @Operation(summary = "회원가입 후 닉네임 설정", description = "회원가입 후 닉네임을 설정합니다. 회원가입 상태 완료로 바뀜")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = NicknameRes.class)))}),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/nickname")
    public ResponseCustom<NicknameRes> saveNickname(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal,
            @Parameter(description = "쿼리 파라미터 형식으로 nickname을 입력해주세요.", required = true) @RequestParam String nickname) {
        return ResponseCustom.OK(authService.updateNickname(userPrincipal, nickname));
    }


//    @PostMapping("/test/login")
//    public ResponseCustom<AuthRes> testlogin(@RequestBody IdTokenReq idTokenReq) {
//
//        System.out.println("idTokenReq = " + idTokenReq.idToken());
//        System.out.println("idTokenReq = " + idTokenReq.provider());
//
//        String accessToken = jwtUtil.createJwt("access", "", "ROLE_USER", 3600000L, "");
//
//        AuthRes authRes = AuthRes.builder()
//                .accessToken(accessToken)
//                .build();
//
//        return ResponseCustom.OK(authRes);
//    }


    @Operation(summary = "회원 탈퇴", description = "해당 유저의 가입을 탈퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Message.class)))}),
            @ApiResponse(responseCode = "400", description = "회원 탈퇴 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping()
    public ResponseCustom<Message> unlinkSocialAccount(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @CurrentUser UserPrincipal userPrincipal
    ) {
        return ResponseCustom.OK(authService.unlinkAccount(userPrincipal.getId()));
    }
}
