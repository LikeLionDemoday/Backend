package com.Dodutch_Server.domain.trip.dto;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TripResponseDTO {
    private Long tripId;        // 여행 ID
    private String tripName;    // 여행 이름
    private String startDate;   // 시작 날짜
    private String endDate;     // 종료 날짜
    private String place;       // 장소
    private Long budget;        // 예산
    private String qrCode;      // QR 코드 (null 가능)
}
