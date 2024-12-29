package com.Dodutch_Server.domain.trip.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {
    private Long tripId;        // 여행 ID
    private String tripName;    // 여행 이름
    private String startDate;   // 시작 날짜
    private String endDate;     // 종료 날짜
    private String place;       // 장소
    private Long budget;        // 예산
    private Integer totalCost; // 추가함
    private String memo;

    private List<MemberDTO> members; // 멤버 리스트 추가

    @Getter
    @Setter
    public static class MemberDTO {
        private Long memberID; // Member ID
    }
}