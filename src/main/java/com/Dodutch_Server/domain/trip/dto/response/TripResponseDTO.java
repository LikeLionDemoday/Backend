package com.Dodutch_Server.domain.trip.dto.response;


import lombok.*;

import java.util.List;


@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripResponseDTO {
    private Long tripId;        // 여행 ID
    private String joinCode; // 참여코드
    private String tripImageUrl;
}

