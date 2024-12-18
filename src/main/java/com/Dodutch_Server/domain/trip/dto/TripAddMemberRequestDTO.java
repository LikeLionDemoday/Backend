package com.Dodutch_Server.domain.trip.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TripAddMemberRequestDTO {
    private Long tripId;  // 여행 ID
    private String qrCode;  // QR 코드
    private Long memberId;  // 멤버 ID 임의로 추가
}
