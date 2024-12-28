package com.Dodutch_Server.domain.dutch.dto;

import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DutchDTO {
    private Long id;
    private Integer perCost;
    private Boolean isCompleted;
    private Trip trip;
    private TripMember tripMember;
    private Long payer;
    private Long payee;

    public DutchDTO(Long id, Integer perCost, Boolean isCompleted, Trip trip, TripMember tripMember, Member payer, Member payee) {
        this.id = id;
        this.perCost = perCost;
        this.isCompleted = isCompleted;
        this.trip = trip;
        this.tripMember = tripMember;
        this.payer = payer.getId();
        this.payee = payee.getId();
    }
}
