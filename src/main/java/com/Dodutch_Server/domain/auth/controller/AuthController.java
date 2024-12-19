package com.Dodutch_Server.domain.auth.controller;

import com.Dodutch_Server.domain.auth.dto.request.KakaoRequestDto;
import com.Dodutch_Server.domain.auth.dto.request.RefreshRequestDto;
import com.Dodutch_Server.domain.auth.dto.request.SignUpRequestDto;
import com.Dodutch_Server.domain.auth.dto.response.KakaoResponseDto;
import com.Dodutch_Server.domain.auth.dto.response.RefreshResponseDto;
import com.Dodutch_Server.domain.auth.service.AuthService;
import com.Dodutch_Server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인 관련된 API")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao/login")
    @Operation(summary = "카카오 로그인 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<KakaoResponseDto> login(@RequestBody KakaoRequestDto kakaoRequestDto,
                                               HttpServletResponse response) {

        KakaoResponseDto kakaoResponseDto = authService.loginWithKakao(kakaoRequestDto.getAccessCode(), response);

        return ApiResponse.onSuccess(kakaoResponseDto);
    }

    @PostMapping("/signup")
    @Operation(summary = "카카오 로그인 후 로그인 폼 받아서 자체 회원가입 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<Object> signup(@RequestBody SignUpRequestDto signUpRequestDto) {

        authService.signup(signUpRequestDto);

        return ApiResponse.onSuccess();
    }

    // 리프레시 토큰으로 액세스토큰 재발급 받는 로직
    @PostMapping("/token/refresh")
    @Operation(summary = "Access Token 재발급 API")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공")
    })
    public ApiResponse<RefreshResponseDto> tokenRefresh(@RequestBody RefreshRequestDto refreshRequestDto, HttpServletResponse httpServletResponse) {

        RefreshResponseDto refreshResponseDto = authService.refreshAccessToken(refreshRequestDto);

        return ApiResponse.onSuccess(refreshResponseDto);

    }

}
