package com.Dodutch_Server.domain.dutch.domain;

import com.Dodutch_Server.domain.trip.domain.TripMember;
import com.Dodutch_Server.domain.member.domain.Member;
import com.Dodutch_Server.domain.trip.domain.Trip;
import jakarta.persistence.*;

@Entity
public class Dutch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer perCost;
    private Boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "tripId", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "tripMemberId", nullable = false)
    private TripMember tripMember;

    @ManyToOne
    @JoinColumn(name = "payerId", nullable = true)
    private Member payer;

    @ManyToOne
    @JoinColumn(name = "payeeId", nullable = true)
    private Member payee;
}
