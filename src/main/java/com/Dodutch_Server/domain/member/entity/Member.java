package com.Dodutch_Server.domain.member.entity;

import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long nickname;
    private Long kakaoId;

    @OneToMany(mappedBy = "member")
    private List<TripMember> tripMembers = new ArrayList<>();
}
