package com.Dodutch_Server.domain.trip.domain;

import com.Dodutch_Server.domain.member.domain.Member;
import jakarta.persistence.*;

@Entity
public class TripMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "tripId", nullable = false)
    private Trip trip;
}
