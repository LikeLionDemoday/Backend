package com.Dodutch_Server.domain.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignUpRequestDto {
    private String nickName;
    private String accessToken;
}
