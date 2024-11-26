package com.Dodutch_Server.domain.member.domain;

import com.Dodutch_Server.domain.trip.domain.TripMember;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nickname;
    private Long kakaoId;

    @OneToMany(mappedBy = "member")
    private List<TripMember> tripMembers = new ArrayList<>();
}
