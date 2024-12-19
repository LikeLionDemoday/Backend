package com.Dodutch_Server.domain.member.entity;

import com.Dodutch_Server.domain.auth.dto.request.SignUpRequestDto;
import com.Dodutch_Server.domain.trip.entity.TripMember;
import com.Dodutch_Server.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickName;

    private String kakaoId;

    private String refreshToken;


    @OneToMany(mappedBy = "member")
    private List<TripMember> tripMembers = new ArrayList<>();

    public void setRefreshToken(String token) {
        this.refreshToken = token;
    }

    public void setNickName(String nickName){
        this.nickName = nickName;
    }
}
