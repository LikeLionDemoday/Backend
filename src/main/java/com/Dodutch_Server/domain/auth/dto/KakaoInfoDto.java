package com.Dodutch_Server.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoInfoDto {
    private String kakaoId;

    public KakaoInfoDto(Map<String, Object> attributes) {
        this.kakaoId = attributes.get("id").toString();
    }
}
