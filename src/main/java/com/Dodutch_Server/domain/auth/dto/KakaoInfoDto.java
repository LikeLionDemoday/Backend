package com.Dodutch_Server.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class KakaoInfoDto {
    private String kakaoId;
    private String name;

    public KakaoInfoDto(Map<String, Object> attributes) {
        this.kakaoId = attributes.get("id").toString();

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        this.name = profile.get("nickname").toString();


    }

}
