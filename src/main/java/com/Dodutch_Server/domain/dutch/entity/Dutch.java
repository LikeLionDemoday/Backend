package com.Dodutch_Server.domain.dutch.entity;

import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.domain.member.entity.Member;
import com.Dodutch_Server.domain.trip.entity.Trip;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;

@Entity
public class Dutch extends BaseEntity {
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
