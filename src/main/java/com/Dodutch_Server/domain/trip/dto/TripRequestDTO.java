package com.Dodutch_Server.domain.trip.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class TripRequestDTO {
    private String tripName;  // 여행 이름
    private LocalDate startDate;  // 시작 날짜
    private LocalDate endDate;  // 종료 날짜
    private String place;  // 여행지
    private Integer budget;  // 예산
    private String memo;
}

