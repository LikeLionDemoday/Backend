package com.Dodutch_Server.domain.auth.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoResponseDto {
    private String accessToken;
    private String refreshToken;
    private boolean existMember;
}