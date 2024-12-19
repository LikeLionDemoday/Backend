package com.Dodutch_Server.domain.auth.dto;

import com.Dodutch_Server.domain.member.entity.Member;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoMemberAndExistDto {
    private Member member;
    private Boolean existMember;
}