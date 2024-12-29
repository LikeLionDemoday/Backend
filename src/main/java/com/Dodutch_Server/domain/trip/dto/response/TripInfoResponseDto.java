package com.Dodutch_Server.domain.trip.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripInfoResponseDto {
    private Long tripId;        // 여행 ID
    private String tripName;    // 여행 이름
    private LocalDate startDate;     // 종료 날짜
    private LocalDate endDate;     // 종료 날짜
    private String place;       // 장소
    private Integer budget;        // 예산
    private Integer totalCost; // 추가함
    private String tripImageUrl; // 여행 대표사진

    private List<MemberDTO> members; // 멤버 리스트 추가
    private List<PhotoDTO> photos;

    @Getter
    @Setter
    public static class MemberDTO {
        private Long memberId; // Member ID
        private String nickName;
    }

    @Getter
    @Setter
    public static class PhotoDTO {
        private String photoUrl;
    }
}