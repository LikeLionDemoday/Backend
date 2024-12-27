package com.Dodutch_Server.domain.member.controller;

import com.Dodutch_Server.domain.auth.util.SecurityUtil;
import com.Dodutch_Server.domain.member.service.MemberService;
import com.Dodutch_Server.global.common.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(
                    summary = "멤버의 닉네임 존재 여부 확인 API",
    description = "멤버의 닉네임이 존재하면 true, 존재하지 않으면 false를 보내는 API 입니다",
    security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/check/nickname")
    public ApiResponse<Boolean> checkNickNameExists(){
        Long memberId = SecurityUtil.getCurrentUserId();

        boolean isNickNameExists = memberService.checkNickNameExists(memberId);

        return ApiResponse.onSuccess(isNickNameExists);
    }
}
