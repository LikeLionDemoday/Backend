package com.Dodutch_Server.domain.trip.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripRequestDTO {
    private String tripName;  // 여행 이름
    private LocalDate startDate;  // 시작 날짜
    private LocalDate endDate;  // 종료 날짜
    private String place;  // 여행지
    private Integer budget;  // 예산
    private MultipartFile tripImage;
}

