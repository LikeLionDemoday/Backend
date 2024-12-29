package com.Dodutch_Server.domain.trip.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TripShareResponseDto {
    private String tripImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String name;
    private String place;
    private String joinCode;

    @Getter
    @Setter
    public static class MemberDTO {
        private Long memberID; // Member ID
    }
}
