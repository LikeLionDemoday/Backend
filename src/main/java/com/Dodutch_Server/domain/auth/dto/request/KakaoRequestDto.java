package com.Dodutch_Server.domain.auth.dto.request;

import lombok.Getter;

// 클라이언트에게 받을 카카오 인가 코드
@Getter
public class KakaoRequestDto {
    private String accessCode;
}